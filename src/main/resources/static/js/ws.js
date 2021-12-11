var stomp;
var roomId;

function initStomp() {
    var socket = new SockJS('http://localhost:8109/ws-reg')
    stomp = Stomp.over(socket)
    stomp.connect()
}

function enterRoom(Id) {

    exitRoom()

    roomId = Id

    stomp.subscribe('/rooms/' + roomId, function (message) {
        console.log(message.body)
    })
}
function exitRoom() {

    stomp.unsubscribe(roomId)
}
function sendData(data) {

    stomp.send('/register-action/' + roomId, {}, data)
}

initStomp();

function test() {
    enterRoom(1);
    sendData('test1')
}