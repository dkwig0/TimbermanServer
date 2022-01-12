var host = 'http://localhost:8109';
var stomp;
var roomId;

var gameActive = false;

setInterval(enemyHitting, 130)

function initStomp() {
    var socket = new SockJS(host + '/ws-reg')
    stomp = Stomp.over(socket)
    stomp.connect()
}

function enterRoom(Id) {

    exitRoom()

    roomId = Id

    stomp.subscribe('/rooms/' + roomId, function (message) {
        let data = JSON.parse(message.body)
        switch (data.message) {
            case "prep":
                startPreparation()
                break
            case "start":
                startGame()
                break
            case "end":
                endGame(data.scores)
                console.log(data)
                break
            case "scores":
                updateScores()
        }
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

function hitTree() {
    if (gameActive) {
        $('.man.left').addClass('hitting')
        chopTree()
        setTimeout(function () {
            $('.man.left').removeClass('hitting')
        }, 150)
        sendData('hit')
    }
}

function chopTree() {
    $('.tree.active .section:nth-of-type(2)').addClass('chopped')
    $('.tree.active').append('<div class="section"><div class="branch"></div></div>')
    setTimeout(function () {
        $('.tree.active .section:nth-of-type(2)').remove()
    }, 110)
}

function enemyHitting() {
    if (gameActive) {
        $('.tree.inactive .section:nth-of-type(2)').addClass('chopped')
        $('.tree.inactive').append('<div class="section"><div class="branch"></div></div>')
        setTimeout(function () {
            $('.tree.inactive .section:nth-of-type(2)').remove()
        }, 110)
        $('.man.right').addClass('hitting')
        setTimeout(function () {
            $('.man.right').removeClass('hitting')
        }, 150)
    }
}

function openFindRoom() {
    loadRooms()
    $('.find-room').addClass('open')
}

function closeFindRoom() {
    $('.find-room').removeClass('open')
}


function openMenu() {
    $('.menu').addClass('open')
}

function closeMenu() {
    $('.menu').removeClass('open')
}

function openLoading() {
    $('.loading').addClass('open')
}

function closeLoading() {
    $('.loading').removeClass('open')
}

function openMenuContainer() {
    $('.menu-container').addClass('open')
}

function closeMenuContainer() {
    $('.menu-container').removeClass('open')
}

function openResults() {
    $('.results').addClass('open')
}

function closeResults() {
    $('.results').removeClass('open')
}

function openPreparation() {
    $('.prep').addClass('open')
}

function closePreparation() {
    $('.prep').removeClass('open')
}

function openStart() {
    $('.start').addClass('open')
}

function closeStart() {
    $('.start').removeClass('open')
}

function loadRooms() {
    $('.find-room .rooms').empty()
    $.ajax({
        type: 'GET',
        url: host + '/rooms',
        success: function (rooms) {
            for (let i = 0; i < rooms.length; i++) {
                $('.find-room .rooms').append('<div class="room">' +
                    '<div class="name text" onclick="joinRoom(' + rooms[i].id + ')">' + rooms[i].scores[0].username + '</div>' +
                    '</div>')
            }
        }
    })
}

function createRoom() {
    closeMenu()
    closeFindRoom()
    openLoading()
    $.ajax({
        type: 'POST',
        url: host + '/rooms',
        success: function (room) {
            enterRoom(room.id)
        },
        error: function () {
            closeLoading()
            openMenu()
        }
    })
}

function joinRoom(id) {
    closeMenu()
    closeFindRoom()
    openLoading()
    $.ajax({
        type: 'PATCH',
        url: host + '/rooms/' + id,
        processData: false,
        contentType: 'application/json-patch+json',
        success: function () {
            enterRoom(id)
            startPreparation()
        },
        error: function () {
            closeLoading()
            openMenu()
        }
    })
}

function startPreparation() {
    gameActive= false
    closeLoading()
    closeMenuContainer()
    openPreparation()
}

function startGame() {
    closePreparation()
    openStart()
    gameActive = true

}

function endGame(scores) {
    closeStart()
    gameActive = false
    openMenuContainer()
    openMenu()
    showResults(scores)
}

function updateScores() {

}

function showResults(scores) {
    $('.results').html(scores[0].username + ":" + scores[0].points + "<br>" +
        scores[1].username + ":" + scores[1].points)
    openResults()
}