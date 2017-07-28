	var strScript = '<script type="text/javascript">if(window.console == undefined){console = {log:function(){}};}</script>\n',
		__defaultPath = '../../js/',
		__arrJsPath = [
								__defaultPath+'kr/co/softbowl/js/Library/json-minified.js',
								__defaultPath+'kr/co/softbowl/js/Library/json2.js',
								__defaultPath+'kr/co/softbowl/js/Library/jquery-1.7.min.js',
								__defaultPath+'kr/co/softbowl/SBGrid.js',
								__defaultPath+'common/sbGrid_common.js',
								__defaultPath+'syntaxhighlighter_3.0.83/scripts/shCore.js',
								__defaultPath+'syntaxhighlighter_3.0.83/scripts/shBrushJScript.js',
								__defaultPath+'syntaxhighlighter_3.0.83/scripts/shBrushJava.js'
							],
		__arrCssPath = [
								__defaultPath+'kr/co/softbowl/css/Sbgrid_default.css',
								__defaultPath+'syntaxhighlighter_3.0.83/styles/shCore.css',
								__defaultPath+'syntaxhighlighter_3.0.83/styles/shThemeDefault.css'
							];
							
	for( var i = 0; i < __arrJsPath.length; i+=1){
		strScript += '<script src="'+ __arrJsPath[i] +'" type="text/javascript"></script>\n';
	}
	for( var i = 0; i < __arrCssPath.length; i+=1){
		strScript += '<link href="'+ __arrCssPath[i] +'" type="text/css" rel="stylesheet"></link>\n';
	}
	document.write(strScript);
