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
//                         "user_details":{"name":"Dipankar","profile_pic":"xyz"...}
//                         "endpoints" = {  "sessoon1": {"device_id":"1234","location":"1234#1234"},
//                                          "sessoon2": {"device_id":"1234","location":"1234#1234"}
//                                       }             
//                          }

var allLiveConn = {}
// it's a map from session to userid.
var SessionToUserID = {}
//it's a map which find all the session involves in a perticuler call
// we can have multiple sessiom(users) - which makes it a conf call.
var CallToSessionList = {}

function getAllSessionForUser(user_id){
    if(allLiveConn[user_id]){
        return Object.keys(allLiveConn[user_id].endpoints)
    } else{
        return []
    }
}

///////// contrats with JAVA  - please Don't mess up
function _getOfferPayload(call_id, sdp, userinfo){
    return {"call_id":call_id,"sdp":sdp,"userinfo":userinfo}
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
io.sockets.on('connection', function (client) {
    log("in", "connection",  client.id);

    /*******************************************************************
     * 
     *     Connect, Register and Diconnect
     * 
     * *****************************************************************/
    // In this section we have connect and disconnect 
    client.on('register', function (data) {
        if(!data.user_id){
            return;
        }
        if(allLiveConn[data.user_id] == undefined){
            if(!data.user_details){
                data.user_details ={}
            }
            allLiveConn[data.user_id] = {"name":data.user_name, "status":"free","endpoints":{},"user_details":data.user_details} 
        }
        // If The Same register is comming from endpoint - You might be calling 
        for( var ep in allLiveConn[data.user_id].endpoints){
            if(ep.device_id == data.device_id){
                invalidRequestDetails = _getInvalidRequestPayload("duplicate","Looks like you are calling register multiple times.")
                sendToSelf("invalid_playload",invalidRequestDetails)
                return;
            }
        }
        //All Good update the endpoints
        var current_endpoint = {"device_id":data.device_id,"device_name":data.device_name, "device_location":data.device_loc}
        var session = client.id;
        allLiveConn[data.user_id]["endpoints"][session] = current_endpoint;
        SessionToUserID[session] = data.user_id;
        log("in", "register",  client.id);
        console.log("[Info]: Now Live "+Object.keys(SessionToUserID).length)
    });
    client.on('disconnect', function (details) {
        log("in", "disconnect",  client.id);
        session = client.id
        user_id = SessionToUserID[session]
        delete SessionToUserID[session]
        if(allLiveConn[user_id]){
            delete allLiveConn[user_id]["endpoints"][session]
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
    client.on('offer', function (details) {
        log("in", "offer",  client.id)
        var call_id = details.call_id
        var user_id = details.user_id
        var session = client.id

        var peer_id = details.peer_id;
        var sdp = details.sdp;

        //caller user offline
        if(!allLiveConn[user_id] || !allLiveConn[user_id].endpoints){
            endCallDetails = _getEndCallPayload(call_id,"offline","Looks like you are not yet register.")
            sendToSelf("endcall",endCallDetails)
            return;
        }
        //caller user busy
        if(allLiveConn[user_id]['status'] =="busy"){
            endCallDetails = _getEndCallPayload(call_id,"busy","Looks like you are are busy in another call")
            sendToSelf("endcall",endCallDetails)
            //TODO:
            allLiveConn[user_id]['status'] = "free";
            return;
        }

        //Peer user offline
        if(!allLiveConn[peer_id] || !allLiveConn[peer_id].endpoints){
        endCallDetails = _getEndCallPayload(call_id,"offline","Looks peer is offline")
        sendToSelf("endcall",endCallDetails)
        return;
        }
        //peer user busy
        if(allLiveConn[user_id]['status'] =="busy"){
            endCallDetails = _getEndCallPayload(call_id,"busy","Looks like peer is busy in another call")
            sendToSelf("endcall",endCallDetails)
            return;
        }

        // All is OK. send the sdp to all endpoint
        var session_list = getAllSessionForUser(peer_id);
        var offerCallDetails = _getOfferPayload(call_id, sdp,details)
        sendToSpacificListExceptSender(session_list, 'offer',offerCallDetails)
        
        // make the user busy and add entry to callToSessionList, note that caller also put into inviews
        allLiveConn[user_id]['status'] = "busy"
        session_list.push(session)
        CallToSessionList[call_id] = {"author":session,"invites":session_list,"accepted":[session],"rejected":[]}
    });

    // Here an endpoint says that a session accepted the offer - it means we should send the ans to author
    // and also send endCall Notification for all other session of that user.
    client.on('answer', function (details) {
        log("in", "answer",  client.id);

        call_id = details.call_id
        session = client.id

        //send Accptance to author
        var ansDeatails = _getAnswerPayload(call_id,details.sdp)
        sendToSpacific(CallToSessionList[call_id].author,"answer",ansDeatails)

        //send end all to all others
        var endDetails = _getEndCallPayload(call_id,'received_by_other_endpoint',"You call is received by other endpoint");
        var session_list = getAllSessionForUser(SessionToUserID[session]);
        sendToSpacificListExceptSender(session_list, "endcall", endDetails);
    });
    
    // We we recv an ice we should send it to all invites - Or Should i sned to only acceptance?
    client.on('candidate', function (details) {
        log("in", "candidate",  client.id);
        if(!CallToSessionList[details.call_id]){
            return;
        }
        var session_list = CallToSessionList[details.call_id]['invites'];
        var icePayload = _getIcePayload(details.sdpMid,details.sdpMLineIndex,details.sdp,details.call_id)
        sendToSpacificListExceptSender(session_list, "candidate",details);
    });


    /*******************************************************************
     * 
     *     End and Reject Calls
     * 
     * *****************************************************************/

    //We had a endCall Message - In thase case we should send endcall to all invites to that call.
    client.on('endcall', function (details) {
        log("in", "endcall",  client.id);
        session = client.id
        user_id = SessionToUserID[session]

        //first make the user free
        if(allLiveConn[user_id]){
            allLiveConn[user_id]['status'] = 'free'
        }
        
        // check if there is a call exist.
        if(CallToSessionList[details.call_id] == undefined){
            return;
        }
        
        var session_list = CallToSessionList[details.call_id]['invites'];
        var endCallDetails = _getEndCallPayload(details.call_id,"user_reject","this call is rejceted by peer")
        sendToSpacificListExceptSender(session_list, "endcall",endCallDetails);

        // get all accpets and mark them free
        var session_list = CallToSessionList[details.call_id]['invites'];
        for( var s in session_list){
            var user = SessionToUserID[s]
            if(user != undefined){
                allLiveConn[user]['status'] = 'free'
            }
        }
        //clean up delete the list
        delete CallToSessionList[details.call_id]
    });

    /*******************************************************************
     * 
     *     Write your test and Experiments
     * 
     * *****************************************************************/

    // Listen for test and disconnect events
    client.on('test', function (details) {
        log("in", "test",  client.id);
        client.emit('test', "Cheers, " + client.id);
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

    function sendToAll(tag, data){
        console.log('--> sendToAll:' + tag);
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

    function sendToAllInRoomExceptSender(room, tag, data){
        console.log('--> sendToAllInRoomExceptSender:' + data);
        client.broadcast.to(room).emit(tag,data);
    }

    function sendToAllInRoomIncludeSender(room, tag, data){
        console.log('--> sendToAllInRoomIncludeSender:' + tag);
        io.in(room).emit(tag, data);
    }

    function sendToSpacific(socketid, tag, data){
        //console.log('--> sendToSpacific:' + tag);
        log("out", tag,  socketid);
        client.broadcast.to(socketid).emit(tag,data);
    }
    function sendToSpacificListExceptSender(socketids, tag, data){
        if(socketids == undefined) return;
        for(socketid of socketids){
            if(socketid == client.id) continue;
            log("out", tag,  socketid);
            //console.log("[Info] Sending messge of type "+tag+" to "+socketid)
            client.broadcast.to(socketid).emit(tag,data);
        }
    }

    // Here starts evertyhing!
    // The first connection doesn't send anything (no other clients)
    // Second connection emits the message to start the SDP negotation
    // client.broadcast.emit('createoffer', {});
});
