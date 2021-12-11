function hitTree() {
    $('.man.left').addClass('hitting')
    chopTree()
    setTimeout(function () {
        $('.man.left').removeClass('hitting')
    }, 150)
}

function chopTree() {
    $('.tree.active .section:nth-of-type(2)').addClass('chopped')
    $('.tree.active').append('<div class="section"><div class="branch"></div></div>')
    setTimeout(function () {
        $('.tree.active .section:nth-of-type(2)').remove()
    }, 110)
}