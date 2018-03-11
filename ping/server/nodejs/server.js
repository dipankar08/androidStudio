var socketIO = require('socket.io');
var server = require('http').createServer().listen(7000, '0.0.0.0');
var io = socketIO.listen(server);

// Super simple server:
//  * One room only. 
//  * We expect two people max. 
//  * No error handling.

var allLiveConn = {}

io.sockets.on('connection', function (client) {
    console.log('new connection: ' + client.id);

    // this is ment for presence protocol
    // when we receved a ping - we need to store the entry, broadcast to others.
    client.on('pingsss', function (data) {
        console.log('ping Received: ' + JSON.stringify(data));
        data.clientid = client.id;
        allLiveConn[data.user_id] = data;
        sendToAll('onlineusers', JSON.stringify({"users":allLiveConn}));
    });

    client.on('offer', function (details) {
        client.broadcast.emit('offer', details);
        console.log('offer: ' + JSON.stringify(details));
    });

    client.on('answer', function (details) {
        client.broadcast.emit('answer', details);
        console.log('answer: ' + JSON.stringify(details));
    });
    
    client.on('candidate', function (details) {
        client.broadcast.emit('candidate', details);
        console.log('candidate: ' + JSON.stringify(details));
    });
    client.on('endcall', function (details) {
        client.broadcast.emit('endcall', details);
        console.log('endcall: ' + JSON.stringify(details));
    });



    // Listen for test and disconnect events
    client.on('test', onTest);
    client.on('disconnect', onDisconnect);

    // Handle a test event from the client
    function onTest(data) {
        console.log('Received: "' + data + '" from client: ' + client.id);
        client.emit('test', "Cheers, " + client.id);
    }

    // Handle a disconnection from the client
    function onDisconnect() {
        console.log('Received: disconnect event from client: ' + client.id);
        client.removeListener('test', onTest);
        client.removeListener('disconnect', onDisconnect);
    }

    //helpers
    function sendToSelf(tag, data){
        console.log('sendToSelf' + data);
        socket.emit(tag, data);
    }

    function sendToAll(tag, data){
        console.log('sendToSelf' + data);
        client.emit(tag, data);
    }

    function sendToAllIncludeSender(tag, data){
        console.log('sendToSelf' + data);
        io.emit(tag, data);
    }

    function sendToAllExceptSender(tag, data){
        console.log('sendToSelf' + data);
        socket.broadcast.emit(tag, data);
    }

    function sendToAllInRoomExceptSender(room, tag, data){
        console.log('sendToSelf' + data);
        socket.broadcast.to(room).emit(tag,data);
    }

    function sendToAllInRoomIncludeSender(room, tag, data){
        console.log('sendToSelf' + data);
        io.in(room).emit(tag, data);
    }

    function sendToSpacific(socketid, tag, data){
        console.log('sendToSelf' + data);
        socket.broadcast.to(socketid).emit(tag,data);
    }

    // Here starts evertyhing!
    // The first connection doesn't send anything (no other clients)
    // Second connection emits the message to start the SDP negotation
    // client.broadcast.emit('createoffer', {});
});
