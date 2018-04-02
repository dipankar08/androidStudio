var socketIO = require('socket.io');
var server = require('http').createServer().listen(7000, '0.0.0.0');
var io = socketIO.listen(server);



// Super simple server:
//  * One room only. 
//  * We expect two people max. 
//  * No error handling.

// Let's define the assumtion How the data is stored:
// Every user has ID and Other info and he might be login to multiple devices - When you
// call, all the device which is correted logged should ring and if any one accept it,
// leads to two things: Make the user status busy and send other as end call.
// if the user is busy and get call - will be endpoint should get the notification as busy from
// server itself, weithout propagation sdp to endpoints.
// A sample structure of it is given below:
// allLiveConn = {"uid1":{ "name": "Dipankar",
//                         "status":"free",
//                         "user_info":{"name":"Dipankar","profile_pic":"xyz"...}
//                         "endpoints" = {  "sessoon1": {"device_id":"1234","location":"1234#1234"},
//                                          "sessoon2": {"device_id":"1234","location":"1234#1234"}
//                                       }             
//                          }

var allLiveConn = {}
// it's a map from session to userid.
var SessionToUserID = {}
//it's a map which find all the session involves in a perticuler call
// we can have multiple sessiom(users) - which makes it a conf call.
var CallToSessionList = {} //{"author":session,"invites":session_list,"accepted":[session],"rejected":[]}

// Helpsrs
function getAllSessionForUser(user_id){
    if(allLiveConn[user_id]){
        return Object.keys(allLiveConn[user_id].endpoints)
    } else{
        return []
    }
}

function _cleanupCall(callid){
    var call = CallToSessionList[callid]
    if(!call) return;
    for( var s of Object.values(call.invites)){
        userid = SessionToUserID[s]
        if(!userid) continue;
        allLiveConn[userid].status = "free"
    }
    delete CallToSessionList[callid];
}

function _getAllEndpointForEndPoint(s){
    var user_id = SessionToUserID[s]
    return Object.keys(allLiveConn[user_id].endpoints);
}

function _getUserInfo(user_id){
    if(user_id && allLiveConn[user_id]){
        return allLiveConn[user_id].user_info
    } else{
        return null;
    }
}
//reply all live user which should visible to user_id
function _getAllLiveUserInfo(user_id){
    if(user_id && allLiveConn[user_id]){
        var result =[]
        for( var key of Object.keys(allLiveConn)){
            if(key == user_id) continue;
            var value = allLiveConn[key]
            result.push(value.user_info)
        }
        return result;
    } else{
        return [];
    }
}



///////// contrats with JAVA  - please Don't mess up
// Please be consisitance with contracts/ICallSignalingApi.java

var RING_TIMEOUT = 15 // ring for 15 sec

var TOPIC_IN_CONNECTION = "connection"
var TOPIC_IN_REGISTER = "register"
var TOPIC_IN_DISCONNECT = "disconnect"
var TOPIC_IN_OFFER = "offer"
var TOPIC_IN_CANDIDATE = "candidate"
var TOPIC_IN_ANSWER = "answer"
var TOPIC_IN_ENDCALL = "endcall"
var TOPIC_IN_TEST = "test"
var TOPIC_IN_NOTI = "notification"
var TOPIC_IN_ADDON = "addon"

var TOPIC_OUT_TEST = "test"
var TOPIC_OUT_OFFER = "offer"
var TOPIC_OUT_CANDIDATE = "candidate"
var TOPIC_OUT_ANSWER = "answer"
var TOPIC_OUT_ENDCALL = "endcall"
var TOPIC_OUT_INVALID_PAYLOAD = "invalid_playload"
var TOPIC_OUT_NOTI = "notification"
var TOPIC_OUT_ADDON = "addon"
var TOPIC_OUT_PRESENCE = "presence"
var TOPIC_OUT_WELCOME = "welcome"

var ENDCALL_TYPE_NORMAL_END= "normal_end"
var ENDCALL_TYPE_SELF_OFFLINE = "self_offline"
var ENDCALL_TYPE_SELF_REJECT = "self_reject"
var ENDCALL_TYPE_SELF_PICKUP = "self_pickup"
var ENDCALL_TYPE_SELF_BUSY = "self_busy"
var ENDCALL_TYPE_SELF_NOTPICKUP = "self_notpickup"
var ENDCALL_TYPE_PEER_OFFLINE = "peer_offline"
var ENDCALL_TYPE_PEER_REJECT = "peer_reject"
var ENDCALL_TYPE_PEER_PICKUP = "peer_pickup"
var ENDCALL_TYPE_PEER_BUSY = "peer_busy"
var ENDCALL_TYPE_PEER_NOTPICKUP = "peer_notpickup"

var PRESENCE_TYPE_ONLINE = "online"
var PRESENCE_TYPE_OFFLINE = "offline"


function _getOfferPayload(call_id, sdp, userinfo,is_video_enabled){
    return {"call_id":call_id,"sdp":sdp,"peer_info":userinfo,"is_video_enabled":is_video_enabled}
}
function _getAnswerPayload(call_id, sdp){
    return {"call_id":call_id,"sdp":sdp}
}
function _getIcePayload(sdpMid, sdpMLineIndex, sdp, call_id){
    return {"sdpMid":sdpMid, "sdpMLineIndex":sdpMLineIndex, "sdp":sdp,"call_id":call_id}
}
function _getEndCallPayload(call_id, type,reason){
    return {"type":type,"call_id":call_id,"reason":reason}
}
function _getInvalidRequestPayload(type,reason){
    return {"type":type,"reason":reason}
}
function _getNotificationPayload(type, msg){
    return {"type":type,"msg":msg}
}
function _getAddonPayload(type, data){
    return {"type":type,"msg":data}
}
function _getPresencePayload(type, user){
    return {"type":type,"user_info":user}
}

function _getWelcomePayload(live_users){
    return {"live_users":live_users}
}

function log(type, ops,  session){
    if(type == 'in'){
        type ="IN"
    } else if(type =="out"){
        type ="OUT"
    }
    // data to be snet
    if(SessionToUserID[session] && allLiveConn[SessionToUserID[session]].endpoints){
        console.log('['+type+'] '+ops+' Call ( session: ' + session+ ", Device:"+ allLiveConn[SessionToUserID[session]].endpoints[session].device_name+")");
    } else{
        console.log('['+type+'] '+ops+' Call ( session: ' + session +")");
    }
}
// Socket handlaer starts here...
io.sockets.on(TOPIC_IN_CONNECTION, function (client) {
    log("in", "connection",  client.id);

    /*******************************************************************
     * 
     *     Connect, Register and Diconnect
     * 
     * *****************************************************************/
    // In this section we have connect and disconnect 
    client.on(TOPIC_IN_REGISTER, function (data) {
        log("in", "register",  client.id);
        if(!data.user_id){
            return;
        }
        var notificationPayload = _getNotificationPayload("connected","You are now connected!")
        if(allLiveConn[data.user_id] == undefined){
            allLiveConn[data.user_id] = {"name":data.user_name, "status":"free","endpoints":{},"user_info":data.user_info} 
        }
        // If The Same register is comming from endpoint - You might be calling 
        for( var ep in allLiveConn[data.user_id].endpoints){
            if(ep.device_id == data.device_id){
                // make client aware of connectd
                endToSelf(TOPIC_OUT_NOTI,notificationPayload)
                invalidRequestDetails = _getInvalidRequestPayload("duplicate","Looks like you are calling register multiple times.")
                sendToSelf(TOPIC_OUT_INVALID_PAYLOAD,invalidRequestDetails)
                return;
            }
        }
        //All Good update the endpoints
        var current_endpoint = {"device_id":data.device_id,"device_name":data.device_name, "device_location":data.device_loc}
        var session = client.id;
        allLiveConn[data.user_id]["endpoints"][session] = current_endpoint;
        SessionToUserID[session] = data.user_id;
        console.log("[Info]: Now Live "+Object.keys(SessionToUserID).length)
        sendToSelf(TOPIC_OUT_NOTI,notificationPayload)

        // send to welcome payload
        var all_liveinfo = _getAllLiveUserInfo(data.user_id);
        if(all_liveinfo){
            var welcome_payload = _getWelcomePayload(all_liveinfo)
            sendToSelf(TOPIC_OUT_WELCOME,welcome_payload)
        }
        
        // Send Avoivlibity onfo to all the live pairs.
        var presence_payload = _getPresencePayload(PRESENCE_TYPE_ONLINE,_getUserInfo(data.user_id))
        sendToAllExceptSender(TOPIC_OUT_PRESENCE,presence_payload)
    });
    
    client.on(TOPIC_IN_DISCONNECT, function (details) {
        log("in", "disconnect",  client.id);
        session = client.id
        user_id = SessionToUserID[session]
        delete SessionToUserID[session]
        if(allLiveConn[user_id]){
            delete allLiveConn[user_id]["endpoints"][session]
            
            // Send Avoivlibity onfo to all the live pairs.
            if(isEmpty(allLiveConn[user_id]["endpoints"])){
                var presence_payload = _getPresencePayload(PRESENCE_TYPE_OFFLINE,_getUserInfo(user_id))
                sendToAllExceptSender(TOPIC_OUT_PRESENCE,presence_payload)
                delete allLiveConn[user_id]
            }
        }
        console.log("[Info]: Now Live "+Object.keys(allLiveConn).length)
    });




    /*******************************************************************
     * 
     *     Offer, Answer and candidate
     * 
     * *****************************************************************/



    // When we recved an offer(send a call request) we can do the following
    // first, check if the user if offline - just send a messege to self as endcall.
    // second, check if the user is busy - if yes Send a endCall with say busy.
    // third, find all the sessions/endpoints and send the offer to all.
    client.on(TOPIC_IN_OFFER, function (details) {
        log("in", "offer",  client.id)
        var call_id = details.call_id
        var user_id = SessionToUserID[client.id]
        var session = client.id
        var is_video_enabled = details.is_video_enabled;

        var peer_id = details.peer_id;
        var sdp = details.sdp;

        //caller user offline
        if(!allLiveConn[user_id] || !allLiveConn[user_id].endpoints){
            endCallDetails = _getEndCallPayload(call_id,ENDCALL_TYPE_SELF_OFFLINE,"Looks like you are not yet register.")
            sendToSelf(TOPIC_OUT_ENDCALL,endCallDetails)
            _cleanupCall(call_id)
            return;
        }
        
        //Toself
        if(user_id == peer_id){
            endCallDetails = _getEndCallPayload(call_id,ENDCALL_TYPE_SELF_BUSY,"Self calling is not supported yet!")
            sendToSelf(TOPIC_OUT_ENDCALL,endCallDetails)
            _cleanupCall(call_id)
            return;
        }
        //caller user busy
        if(allLiveConn[user_id]['status'] =="busy"){
            endCallDetails = _getEndCallPayload(call_id,ENDCALL_TYPE_SELF_BUSY,"Looks like you are are busy in another call")
            sendToSelf(TOPIC_OUT_ENDCALL,endCallDetails)
            _cleanupCall(call_id)
            return;
        }

        //Peer user offline
        if(!allLiveConn[peer_id] || !allLiveConn[peer_id].endpoints){
            endCallDetails = _getEndCallPayload(call_id,ENDCALL_TYPE_PEER_OFFLINE,"Looks peer is offline")
            sendToSelf(TOPIC_OUT_ENDCALL,endCallDetails)
            _cleanupCall(call_id)
            return;
        }
        //peer user busy
        if(allLiveConn[user_id]['status'] =="busy"){
            endCallDetails = _getEndCallPayload(call_id,ENDCALL_TYPE_PEER_BUSY,"Looks like peer is busy in another call")
            sendToSelf(TOPIC_OUT_ENDCALL,endCallDetails)
            _cleanupCall(call_id)
            return;
        }

        // All is OK. send the sdp to all endpoint
        var session_list = getAllSessionForUser(peer_id);
        var offerCallDetails = _getOfferPayload(call_id, sdp,allLiveConn[user_id].user_info, is_video_enabled)
        sendToSpacificListExceptSender(session_list, TOPIC_OUT_OFFER,offerCallDetails)
        
        // make the user busy and add entry to callToSessionList, note that caller also put into inviews
        session_list.push(session)
        CallToSessionList[call_id] = {"author":session,"invites":session_list,"accepted":[session],"rejected":[]}
        allLiveConn[user_id]['status'] = "busy"
        //set a callback for auto cancel.
        setTimeout(autoCancel, RING_TIMEOUT*1000, call_id);
    });

    // Here an endpoint says that a session accepted the offer - it means we should send the ans to author
    // and also send endCall Notification for all other session of that user.
    client.on(TOPIC_IN_ANSWER, function (details) {
        log("in", "answer",  client.id);

        call_id = details.call_id
        session = client.id
        var user_id = SessionToUserID[details.call_id]

        var call = CallToSessionList[call_id]
        if(!call) return;
        // update call info
        call.accepted.push(session)
        //send Accptance to author
        var ansDeatails = _getAnswerPayload(call_id,details.sdp)
        sendToSpacific(call.author,TOPIC_OUT_ANSWER,ansDeatails)

        //send end all to all others
        var endDetails = _getEndCallPayload(call_id,ENDCALL_TYPE_SELF_PICKUP,"You call is received by other endpoint");
        var session_list = getAllSessionForUser(SessionToUserID[session]);
        sendToSpacificListExceptSender(session_list, TOPIC_OUT_ENDCALL, endDetails);
    });
    
    // We we recv an ice we should send it to all invites - Or Should i sned to only acceptance?
    client.on(TOPIC_IN_CANDIDATE, function (details) {
        //log("in", "candidate",  client.id);
        if(!CallToSessionList[details.call_id]){
            return;
        }
        var session_list = CallToSessionList[details.call_id]['invites'];
        var icePayload = _getIcePayload(details.sdpMid,details.sdpMLineIndex,details.sdp,details.call_id)
        sendToSpacificListExceptSender(session_list, TOPIC_OUT_CANDIDATE,details);
    });

    // In case of addon we just forword to all invite.
    client.on(TOPIC_IN_ADDON, function (details) {
        if(!CallToSessionList[details.call_id]){
            return;
        }
        if(details.type && details.data){
            var session_list = CallToSessionList[details.call_id]['invites'];
            sendToSpacificListExceptSender(session_list, TOPIC_OUT_ADDON, details);
        } else{
            sendToSelf(TOPIC_OUT_INVALID_PAYLOAD,_getInvalidRequestPayload("invalid","The addon doent have type and data"))
        }
    });


    /*******************************************************************
     * 
     *     End and Reject Calls
     * 
     * *****************************************************************/

    //We had a endCall Message - In thase case we should send endcall to all invites to that call.
    client.on(TOPIC_IN_ENDCALL, function (details) {
        log("in", "endcall",  client.id);
        var session = client.id
        var user_id = SessionToUserID[details.call_id]


        // check if there is a call exist.
        var callinfo = CallToSessionList[details.call_id];
        if(!callinfo) {
            return;
        }

        var type = details.type
        switch(type) {
            case ENDCALL_TYPE_PEER_REJECT:
                // Notify author
                var endCallDetails1 = _getEndCallPayload(details.call_id,ENDCALL_TYPE_PEER_REJECT,"This call is rejceted by your friend")
                sendToSpacific(callinfo.author,TOPIC_OUT_ENDCALL, endCallDetails1)
                // Notify Other endpoint
                var endCallDetails1 = _getEndCallPayload(details.call_id,ENDCALL_TYPE_SELF_REJECT,"You have rejceted this call from other mobile")
                sendToSpacificListExceptSender(_getAllEndpointForEndPoint(session),TOPIC_OUT_ENDCALL, endCallDetails1)
                //clean up
                _cleanupCall(details.call_id);
                break;
            case ENDCALL_TYPE_PEER_NOTPICKUP:
                // Notify author
                var endCallDetails1 = _getEndCallPayload(details.call_id,ENDCALL_TYPE_PEER_NOTPICKUP,"This call is not pickedup by your friend")
                sendToSpacific(callinfo.author,TOPIC_OUT_ENDCALL, endCallDetails1)
                // Notify Other endpoint
                var endCallDetails1 = _getEndCallPayload(details.call_id,ENDCALL_TYPE_SELF_NOTPICKUP,"This call is pickup by your friend.")
                sendToSpacificListExceptSender(_getAllEndpointForEndPoint(session),TOPIC_OUT_ENDCALL, endCallDetails1)
                //clean up
                _cleanupCall(details.call_id);
                break;
            case ENDCALL_TYPE_PEER_BUSY:
                // Notify author
                var endCallDetails1 = _getEndCallPayload(details.call_id,ENDCALL_TYPE_PEER_BUSY,"This call is rejceted by your friend")
                sendToSpacific(callinfo.author,TOPIC_OUT_ENDCALL, endCallDetails1)
                _cleanupCall(details.call_id);
                break;
            case ENDCALL_TYPE_NORMAL_END:
                // Notify all as tis call ends normaly
                var endCallDetails1 = _getEndCallPayload(details.call_id,ENDCALL_TYPE_NORMAL_END,"This call is ended by either you or by your friend.")
                sendToSpacificListExceptSender(callinfo.invites,TOPIC_OUT_ENDCALL, endCallDetails1)
                _cleanupCall(details.call_id);
                break;
            default:

            _cleanupCall(details.call_id);
        }
        // double check to cleanup the call
        _cleanupCall(details.call_id);
    });

    /*******************************************************************
     * 
     *     Write your test and Experiments
     * 
     * *****************************************************************/

    // Listen for test and disconnect events
    client.on(TOPIC_IN_TEST, function (details) {
        log("in", "test",  client.id);
        client.emit(TOPIC_OUT_TEST, "Cheers, " + client.id);
    });
    
 
 
    /*******************************************************************
     * 
     *     Helper functions
     * 
     * *****************************************************************/
    //helpers
    function sendToSelf(tag, data){
        log("out", tag,  client.id);
        //console.log('--> sendToSelf:' + tag);
        client.emit(tag, data);
    }

    function sendToAllIncludeSender(tag, data){
        console.log('--> sendToAllIncludeSender:' + tag);
        io.emit(tag, data);
    }

    function sendToAllExceptSender(tag, data){
        console.log('--> sendToAllExceptSender:' + tag);
        client.broadcast.emit(tag, data);
    }
/*
    function sendToAllInRoomExceptSender(room, tag, data){
        console.log('--> sendToAllInRoomExceptSender:' + data);
        client.broadcast.to(room).emit(tag,data);
    }
    function sendToAllInRoomIncludeSender(room, tag, data){
        console.log('--> sendToAllInRoomIncludeSender:' + tag);
        io.in(room).emit(tag, data);
    }
*/
    function sendToSpacific(socketid, tag, data){
        //console.log('--> sendToSpacific:' + tag);
        log("out", tag,  socketid);
        client.broadcast.to(socketid).emit(tag,data);
    }
    //include sender
    function sendToSpacificListIncludeSender(socketids, tag, data){
        if(socketids == undefined) return;
        for(socketid of socketids){
            log("out", tag,  socketid);
            if(client.id == socketid){
                client.emit(tag, data);
            } else{
                client.broadcast.to(socketid).emit(tag,data);
            }
        }
    }
    //exclude sender
    function sendToSpacificListExceptSender(socketids, tag, data){
        if(socketids == undefined) return;
        for(socketid of socketids){
            if(socketid == client.id) continue;
            log("out", tag,  socketid);
            //console.log("[Info] Sending messge of type "+tag+" to "+socketid)
            client.broadcast.to(socketid).emit(tag,data);
        }
    }
    //Some Helper
    function autoCancel(call_id) {
        var call = CallToSessionList[call_id];
        if(!call) return;
        if(call.accepted.length > 1) return;
        //auto dismis
        sendToSpacificListIncludeSender(call.invites, TOPIC_OUT_ENDCALL,
             _getEndCallPayload(call_id,ENDCALL_TYPE_PEER_NOTPICKUP,"Your frined has not peaked up this call!"));
        _cleanupCall(call_id)
    }
});


//jshelper
function isEmpty(obj) {
    return Object.keys(obj).length === 0;
}