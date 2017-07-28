$(function () {

    $('#side-menu').metisMenu();

});

//Loads the correct sidebar on window load,
//collapses the sidebar on window resize.
// Sets the min-height of #page-wrapper to window size
$(function () {
    $(window).bind("load resize", function () {
        topOffset = 50;
        width = (this.window.innerWidth > 0) ? this.window.innerWidth : this.screen.width;
        if (width < 768) {
            $('div.navbar-collapse').addClass('collapse');
            topOffset = 100; // 2-row-menu
        } else {
            $('div.navbar-collapse').removeClass('collapse');
        }

        height = ((this.window.innerHeight > 0) ? this.window.innerHeight : this.screen.height) - 1;
        height = height - topOffset;
        if (height < 1) height = 1;
        if (height > topOffset) {
            $("#page-wrapper").css("min-height", (height) + "px");
        }
    });

    var url = window.location;
    var element = $('ul.nav a').filter(function () {
        return this.href == url || url.href.indexOf(this.href) == 0;
    }).addClass('active').parent().parent().addClass('in').parent();
    if (element.is('li')) {
        element.addClass('active');
    }
});

$(function() {
    $("input:text").keydown(function(evt) {
        if (evt.keyCode == 13)
            return false;
    });

    $('.admin-main-icon').mouseover(function(){
        var getImgUrl = $(this).attr('src');
        //var len = getImgUrl.length;
        //var front = getImgUrl.substring(0, len-4);
        var front = getImgUrl.split('.',1);
        //console.log(front);
        $(this).attr('src', front+'2.png');
    });

    $('.admin-main-icon').mouseout(function(){
        var getImgUrl = $(this).attr('src');
        //var len = getImgUrl.length;
        //var front = getImgUrl.substring(0, len-5);
        var front = getImgUrl.split('2.',1);
        //console.log(front);
        $(this).attr('src', front+'.png');
    });




});
