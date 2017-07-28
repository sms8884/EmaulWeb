$(function() {
	$.datepicker.regional['ko'] = {
			closeText: '닫기',
			prevText: '',
			nextText: '',
			currentText: '오늘',
			monthNames: ['1','2','3','4','5','6','7','8','9','10','11','12'],
			monthNamesShort: ['1','2','3','4','5','6','7','8','9','10','11','12'],
			dayNames: ['일','월','화','수','목','금','토'],
			dayNamesShort: ['일','월','화','수','목','금','토'],
			dayNamesMin: ['일','월','화','수','목','금','토'],
			weekHeader: 'Wk',
			dateFormat: 'yy-mm-dd',
			firstDay: 0,
			showButtonPanel : false,	
			isRTL: false,
			yearSuffix: '년',
			monthSuffix: '월',
			changeYear: false,
			changeMonth: false,
			showMonthAfterYear: true,
			showOtherMonths: true,
			showOn: "both",
			buttonText:"달력",
			buttonImage: "../../images/datepicker/calendar_icon.gif",
			buttonImageOnly: true
	};
	
	$.datepicker.setDefaults($.datepicker.regional['ko']);
});