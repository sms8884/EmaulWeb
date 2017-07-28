$(function(){
/* $('.tab>li').click(function(){
	$('.tab>li>a').removeClass('on');
	$(this).find('>a').addClass('on');
	$('.content>span').hide();
	$('.content>span').eq($(this).index()).show();//이미지번호를 불러오는게 아니고 tab>li의 인덱스 각각의 순서를 불러온거임
 })*/
 $('.tab>li').click(function(){
	var num=$(this).index();
	 $('.tab>li').each(function(){
		if(num==$(this).index()){
		$(this).find('a').css({'background-color':'#e4e4e4'})
		}else{
		$(this).find('a').css({'background-color':'#c3c3c3'})
		}
	 })
	$('.content>div').hide();
	$('.content>div').eq($(this).index()).show();//이미지번호를 불러오는게 아니고 tab>li의 인덱스 각각의 순서를 불러온거임
 })
})