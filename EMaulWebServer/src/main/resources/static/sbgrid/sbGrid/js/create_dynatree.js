
$(function(){

	var defaultPath	= './page';
	var detailPath		= ['/00_create', '/01_attribute', '/02_method', '/03_event', '/04_style', '/05_cmmn_sample', '/06_etc_sample'];
	
	$("#tree").dynatree({
		onActivate: function(node) {
			// A DynaTreeNode object is passed to the activation handler
			// Note: we also get this event, if persistence is on, and the page is reloaded.
			//alert("You activated " + node.data.title);
		},
		onClick: function(node) {
			if( node.data.url )
				window.open(node.data.url, node.data.target);
			/*
			if( node.data.title == "+ expand all +"){
				$("#tree").dynatree("getRoot").visit(function(node){
					node.expand(true);
				});
			}
			if( node.data.title == "- collapse all -"){
				$("#tree").dynatree("getRoot").visit(function(node){
					node.expand(false);
				});
			}
			*/
			if( node.data.title == "<table border='1' ><tr><td>+ expand all +</td></tr></table>"){
				$("#tree").dynatree("getRoot").visit(function(node){
					node.expand(true);
				});
			}
			if( node.data.title == "<table border='1' ><tr><td>- collapse all -</td></tr></table>"){
				$("#tree").dynatree("getRoot").visit(function(node){
					node.expand(false);
				});
			}
		},
		persist: true,
		children: [ // Pass an array of nodes.
			//{title: "<table border='1' ><tr><td>+ expand all +</td></tr></table>"},
			//{title: "<table border='1' ><tr><td>- collapse all -</td></tr></table>"},
			{title: "00 생성", isFolder: true, expand: false,
				children:[
					{title: "Step1 : 라이브러리 추가",			url: defaultPath+detailPath[0]+"/00_step1.html",	target:"frmMain"},
					{title: "Step2 : 그리드 객체 생성",			url: defaultPath+detailPath[0]+"/01_step2.html",	target:"frmMain"},
					{title: "Step3 : 컬럼 생성",					url: defaultPath+detailPath[0]+"/02_step3.html",	target:"frmMain"}
				]
			},
			{title: "01 Attribute", isFolder: true, expand: false,
				children:[
					{title: "00 설정",			url: defaultPath+detailPath[1]+"/00_setting.html",				target:"frmMain"},
					{title: "01 정렬",			url: defaultPath+detailPath[1]+"/01_strExplorerbar.html",	target:"frmMain"},
					{title: "02 행높이",		url: defaultPath+detailPath[1]+"/02_nDataHeght.html",		target:"frmMain"},
					{title: "03 선택모드",		url: defaultPath+detailPath[1]+"/03_strSelectMode.html",	target:"frmMain"},
					{title: "04 틀고정",		url: defaultPath+detailPath[1]+"/04_nFrozenRow.html",		target:"frmMain"}
				]
			},
			{title: "02 Method", isFolder: true, expand: false,
				children:[
					{title: "00 사용법",						url: defaultPath+detailPath[2]+"/00_index.html",			target:"frmMain"},
					{title: "01 get / setRow(setCol)",		url: defaultPath+detailPath[2]+"/01_getIndex.html",		target:"frmMain"},
					{title: "02 get / setTextMatrix",		url: defaultPath+detailPath[2]+"/02_getValue.html",		target:"frmMain"},
					{title: "03 insertRow",					url: defaultPath+detailPath[2]+"/03_insertRow.html",		target:"frmMain"},
					{title: "04 deleteRow",					url: defaultPath+detailPath[2]+"/04_deleteRow.html",	target:"frmMain"}
				]
			},
			{title: "03 Event", isFolder: true, expand: false,
				children:[
					{title: "00 적용",		url: defaultPath+detailPath[3]+"/00_index.html",			target:"frmMain"},
					{title: "01 예제",		url: defaultPath+detailPath[3]+"/01_example.html",		target:"frmMain"}
				]
			},
			{title: "04 Style", isFolder: true, expand: false,
				children:[
					{title: "00 적용",        url: defaultPath+detailPath[4]+"/00_index.html",		target:"frmMain"},
					{title: "01 종류",        url: defaultPath+detailPath[4]+"/01_kind.html",		target:"frmMain"},
				]
			},
			{title: "05 공통샘플", isFolder: true, expand: false,
				children:[
					{title: "00 그리드생성",			url: defaultPath+detailPath[5]+"/00 commonGrid.html",			target:"frmMain"},
					{title: "01 행추가",				url: defaultPath+detailPath[5]+"/01_insertRow.html",				target:"frmMain"},
					{title: "02 행삭제",				url: defaultPath+detailPath[5]+"/02_deleteRow.html",			target:"frmMain"},
					{title: "03 행선택",				url: defaultPath+detailPath[5]+"/03_selectRow.html",				target:"frmMain"},
					{title: "04 그리드트리",	url: defaultPath+detailPath[5]+"/04_makeGridTree.html",		target:"frmMain"},
					{title: "05 팝업창샘플",			url: defaultPath+detailPath[5]+"/05_cmGridPopup.html",		target:"frmMain"},
					{title: "06 멀티업데이트",		url: defaultPath+detailPath[5]+"/06_multiUpdate.html",			target:"frmMain"},
					{title: "07 행상하이동",			url: defaultPath+detailPath[5]+"/07_colMove.html",				target:"frmMain"},
					{title: "08 행복사",				url: defaultPath+detailPath[5]+"/08_colCopy.html",				target:"frmMain"}
				]
			},
			{title: "06 응용샘플", isFolder: true, expand: false,
				children:[
					{title: "00 페이징",				url: defaultPath+detailPath[6]+"/00_paging.html",						target:"frmMain"},
					{title: "01 게시판템플릿",		url: defaultPath+detailPath[6]+"/01_bbsSample.html",			target:"frmMain"},
					{title: "02 Dom데이터바인딩",		url: defaultPath+detailPath[6]+"/02_htmlBindSample.html",		target:"frmMain"},
					{title: "03 그리드간데이터이동",		url: defaultPath+detailPath[6]+"/03_gridDataMove.html",		target:"frmMain"}
				]
			}
		],
		fx: {height:"toggle",duration: 400}
	});
});