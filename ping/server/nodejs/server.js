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
    return allLiveConn[user_id][endpoints].keys()
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


// Socket handlaer starts here...
io.sockets.on('connection', function (client) {
    console.log('<-- new connection: ' + client.id);

    // You must do a registration for geting any callback
    client.on('register', function (data) {
        console.log('<-- register Received: ' + JSON.stringify(data));
        if(!data.user_id){
            return;
        }
        if(allLiveConn[data.user_id] == undefined){
            if(!data.user_details){
                data.user_details ={}
            }
            allLiveConn[data.user_id] = {"name":data.user_name, "status":"free","endpoints":{},"user_details":data.user_details} 
        }
        var current_endpoint ={"device_id":data.device_id, "device_location":data.location}
        var session = client.id;
        allLiveConn[data.user_id]["endpoints"][session] = current_endpoint;
        SessionToUserID[session] = data.user_id;
        console.log("register: "+JSON.stringify(allLiveConn))
        //sendToAll('onlineusers', JSON.stringify({"users":allLiveConn}));
    });

    // When we recved an offer(send a call request) we can do the following
    // first, check if the user if offline - just send a messege to self as endcall.
    // second, check if the user is busy - if yes Send a endCall with say busy.
    // third, find all the sessions/endpoints and send the offer to all.
    client.on('offer', function (details) {
        console.log('<-- offer: ' + JSON.stringify(details));
        var call_id = details.call_id
        var user_id = details.user_id
        var session = client.id

        var peer_id = details.peer_id;
        var sdp = details.sdp;

        //user offline
        if(SessionToUserID['peer_id'] == undefined){
            endCallDetails = _getEndCallPayload(call_id,"offline","The peer is offline")
            sendToSelf("endcall",endCallDetails)
            return;
        }
        //user busy
        if(allLiveConn[user_id]['status'] =="busy"){
            endCallDetails = _getEndCallPayload(call_id,"busy","This user is busy")
            sendToSelf("endcall",endCallDetails)
            return;
        }
        // All is OK. send the sdp to all endpoint
        var session_list = getAllSessionForUser(peer_id);
        var details = "todo" //JSON.stringify(details)
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
        console.log('<-- answer: ' + JSON.stringify(details));

        call_id = details.call_id
        session = client.id

        //send Accptance to author
        var ansDeatails = _getAnswerPayload(call_id,details.sdp)
        sendToSpacific(CallToSessionList[call_id].author,"answer",ansDeatails)

        //send end all to all others
        var endDetails ={}
        var session_list = getAllSessionForUser(SessionToUserID[session]);
        sendToSpacificListExceptSender(session_list, "endcall", endDetails);
    });
    
    // We we recv an ice we should send it to all invites - Or Should i sned to only acceptance?
    client.on('candidate', function (details) {
        console.log('<-- candidate: ' + JSON.stringify(details));
        if(!CallToSessionList[details.call_id]){
            return;
        }
        var session_list = CallToSessionList[details.call_id]['invites'];
        var icePayload = _getIcePayload(details.sdpMid,details.sdpMLineIndex,details.sdp,details.call_id)
        sendToSpacificListExceptSender(session_list, "candidate",details);
    });

    //We had a endCall Message - In thase case we should send endcall to all invites to that call.
    client.on('endcall', function (details) {
        console.log('<-- endcall: ' + JSON.stringify(details));
        if(CallToSessionList[details.call_id] == undefined){
            return;
        }
        var session_list = CallToSessionList[details.call_id]['invites'];
        sendToSpacificListExceptSender(session_list, "endcall",details);

        // get all accpets and mark them free
        var session_list = CallToSessionList[details.call_id]['accepted'];
        for( var s in session_list){
            var user = SessionToUserID[s]
            if(user != undefined){
                allLiveConn[user]['status'] = 'free'
            }
        }
        //clean up delete the list
        delete CallToSessionList[details.call_id]

    });


    // Listen for test and disconnect events
    client.on('test', function (details) {
        console.log('<-- Test: "' + data + '" from client: ' + client.id);
        client.emit('test', "Cheers, " + client.id);
    });
    client.on('disconnect', function (details) {
        console.log('<-- disconnect: from' + client.id);
        session = client.id
        user_id = SessionToUserID[session]
        delete SessionToUserID[session]
        if(allLiveConn[user_id]){
            delete allLiveConn[user_id]["endpoints"][session]
        }
        console.log("Data after disconnect"+JSON.stringify(allLiveConn))
    });
 

    //helpers
    function sendToSelf(tag, data){
        console.log('--> sendToSelf:' + tag);
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
        console.log('--> sendToSpacific:' + tag);
        client.broadcast.to(socketid).emit(tag,data);
    }
    function sendToSpacificListExceptSender(socketids, tag, data){
        if(socketids == undefined) return;
        console.log('--> sendToSpacificListExceptSender: ' + tag);
        for(socketid of socketids){
            if(socketid == client.id) continue;
            client.broadcast.to(socketid).emit(tag,data);
        }
    }

    // Here starts evertyhing!
    // The first connection doesn't send anything (no other clients)
    // Second connection emits the message to start the SDP negotation
    // client.broadcast.emit('createoffer', {});
});
