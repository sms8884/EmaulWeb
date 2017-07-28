(function($){
	/* 팝업화면을 click 이벤트에 등록시킨다. */
	$.fn.popupWindow = function(instanceSettings){
		
		return this.each(function(){
		
			$(this).click(function(){
		
				$.fn.popupWindow.defaultSettings = {
					centerBrowser:1, // center window over browser window? {1 (YES) or 0 (NO)}. overrides top and left
					centerScreen:0, // center window over entire screen? {1 (YES) or 0 (NO)}. overrides top and left
					height:500, // sets the height in pixels of the window.
					left:0, // left position when the window appears.
					location:0, // determines whether the address bar is displayed {1 (YES) or 0 (NO)}.
					menubar:0, // determines whether the menu bar is displayed {1 (YES) or 0 (NO)}.
					resizable:0, // whether the window can be resized {1 (YES) or 0 (NO)}. Can also be overloaded using resizable.
					scrollbars:0, // determines whether scrollbars appear on the window {1 (YES) or 0 (NO)}.
					status:0, // whether a status line appears at the bottom of the window {1 (YES) or 0 (NO)}.
					width:500, // sets the width in pixels of the window.
					windowName:null, // name of window set from the name attribute of the element that invokes the click
					windowURL:null, // url used for the popup
					top:0, // top position when the window appears.
					toolbar:0 // determines whether a toolbar (includes the forward and back buttons) is displayed {1 (YES) or 0 (NO)}.
					,type:0	// 0: window.popup, 1 : window.showModalDialog, 2 : window.showModalDialog(return)
				};
		
				settings = $.extend({}, $.fn.popupWindow.defaultSettings, instanceSettings || {});
				if( settings.type == 1 )
				{
					var windowFeatures =    'dialogHeight:' + settings.height + 'px' +
											';dialogWidth:' + settings.width + 'px' +
											';scroll:' + settings.scrollbars +
											';status:' + settings.status + 
											';resizable:' + settings.resizable;
					settings.windowName = this.name || settings.windowName;
					settings.windowURL = this.href || settings.windowURL;
					var centeredY,centeredX;
					if(settings.centerBrowser){
						
						if ($.browser.msie) {//hacked together for IE browsers
							centeredY = (window.screenTop - 120) + ((((document.documentElement.clientHeight + 120)/2) - (settings.height/2)));
							centeredX = window.screenLeft + ((((document.body.offsetWidth + 20)/2) - (settings.width/2)));
						}else{
							centeredY = window.screenY + (((window.outerHeight/2) - (settings.height/2)));
							centeredX = window.screenX + (((window.outerWidth/2) - (settings.width/2)));
						}
						centeredY = parseInt(centeredY);
						centeredX = parseInt(centeredX);
						
						window.showModalDialog(settings.windowURL, window, windowFeatures + ';dialogLeft:' + centeredX + ';dialogTop:' + centeredY);
					}else if(settings.centerScreen){
						centeredY = (screen.height - settings.height)/2;
						centeredX = (screen.width - settings.width)/2;
						centeredY = parseInt(centeredY);
						centeredX = parseInt(centeredX);
						
						window.showModalDialog(settings.windowURL, window, windowFeatures + ';dialogLeft:' + centeredX + ';dialogTop:' + centeredY);
					}else{
						window.showModalDialog(settings.windowURL, window, windowFeatures + ';dialogLeft:' + settings.left + ';dialogTop:' + settings.top);
					}
					return false;
				}
				else if( settings.type == 0 )
				{
					var windowFeatures =    'height=' + settings.height +
											',width=' + settings.width +
											',toolbar=' + settings.toolbar +
											',scrollbars=' + settings.scrollbars +
											',status=' + settings.status + 
											',resizable=' + settings.resizable +
											',location=' + settings.location +
											',menuBar=' + settings.menubar;
					settings.windowName = this.name || settings.windowName;
					settings.windowURL = this.href || settings.windowURL;
					var centeredY,centeredX;
					
					if(settings.centerBrowser){
							
						if ($.browser.msie) {//hacked together for IE browsers
							centeredY = (window.screenTop - 120) + ((((document.documentElement.clientHeight + 120)/2) - (settings.height/2)));
							centeredX = window.screenLeft + ((((document.body.offsetWidth + 20)/2) - (settings.width/2)));
						}else{
							centeredY = window.screenY + (((window.outerHeight/2) - (settings.height/2)));
							centeredX = window.screenX + (((window.outerWidth/2) - (settings.width/2)));
						}
						centeredY = parseInt(centeredY);
						centeredX = parseInt(centeredX);
						
						window.open(settings.windowURL, settings.windowName, windowFeatures+',left=' + centeredX +',top=' + centeredY);
					}else if(settings.centerScreen){
						centeredY = (screen.height - settings.height)/2;
						centeredX = (screen.width - settings.width)/2;
						centeredY = parseInt(centeredY);
						centeredX = parseInt(centeredX);
						
						window.open(settings.windowURL, settings.windowName, windowFeatures+',left=' + centeredX +',top=' + centeredY);
					}else{
						window.open(settings.windowURL, settings.windowName, windowFeatures+',left=' + settings.left +',top=' + settings.top);	
					}
					return false;
				}
				else if( settings.type == 1 )
				{
					var windowFeatures =    'dialogHeight:' + settings.height + 'px' +
											';dialogWidth:' + settings.width + 'px' +
											';scroll:' + settings.scrollbars +
											';status:' + settings.status + 
											';resizable:' + settings.resizable;
					settings.windowName = this.name || settings.windowName;
					settings.windowURL = this.href || settings.windowURL;
					var centeredY,centeredX;
					var varParam = new Object();
					if(settings.centerBrowser){
						
						if ($.browser.msie) {//hacked together for IE browsers
							centeredY = (window.screenTop - 120) + ((((document.documentElement.clientHeight + 120)/2) - (settings.height/2)));
							centeredX = window.screenLeft + ((((document.body.offsetWidth + 20)/2) - (settings.width/2)));
						}else{
							centeredY = window.screenY + (((window.outerHeight/2) - (settings.height/2)));
							centeredX = window.screenX + (((window.outerWidth/2) - (settings.width/2)));
						}
						centeredY = parseInt(centeredY);
						centeredX = parseInt(centeredX);
						
						return window.showModalDialog(settings.windowURL, varParam, windowFeatures + ';dialogLeft:' + centeredX + ';dialogTop:' + centeredY);
					}else if(settings.centerScreen) {
						centeredY = (screen.height - settings.height)/2;
						centeredX = (screen.width - settings.width)/2;
						centeredY = parseInt(centeredY);
						centeredX = parseInt(centeredX);
						
						return window.showModalDialog(settings.windowURL, varParam, windowFeatures + ';dialogLeft:' + centeredX + ';dialogTop:' + centeredY);
					}else{
						return window.showModalDialog(settings.windowURL, varParam, windowFeatures + ';dialogLeft:' + settings.left + ';dialogTop:' + settings.top);
					}
					return false;
				}
				
			});
			
		});	
	};
	
	
	/* 팝업화면을 바로 실행시킨다. */
	$.directPopupWindow = function(instanceSettings){
		var winObj;
		$.directPopupWindow.defaultSettings = {
			centerBrowser:1, // center window over browser window? {1 (YES) or 0 (NO)}. overrides top and left
			centerScreen:0, // center window over entire screen? {1 (YES) or 0 (NO)}. overrides top and left
			height:0, // sets the height in pixels of the window.
			left:0, // left position when the window appears.
			location:0, // determines whether the address bar is displayed {1 (YES) or 0 (NO)}.
			menubar:0, // determines whether the menu bar is displayed {1 (YES) or 0 (NO)}.
			resizable:0, // whether the window can be resized {1 (YES) or 0 (NO)}. Can also be overloaded using resizable.
			scrollbars:0, // determines whether scrollbars appear on the window {1 (YES) or 0 (NO)}.
			status:0, // whether a status line appears at the bottom of the window {1 (YES) or 0 (NO)}.
			width:0, // sets the width in pixels of the window.
			windowName:null, // name of window set from the name attribute of the element that invokes the click
			windowURL:null, // url used for the popup
			top:0, // top position when the window appears.
			toolbar:0 // determines whether a toolbar (includes the forward and back buttons) is displayed {1 (YES) or 0 (NO)}.
			,type:0	// 0: window.popup, 1 : window.showModalDialog, 2 : window.showModalDialog(return)
		};
		
		settings = $.extend({}, $.directPopupWindow.defaultSettings, instanceSettings || {});
		
		if( settings.type == 1 )
		{
			var windowFeatures =    'dialogHeight:' + settings.height + 'px' +
									';dialogWidth:' + settings.width + 'px' +
									';scroll:' + settings.scrollbars +
									';status:' + settings.status + 
									';resizable:' + settings.resizable;
			settings.windowName = this.name || settings.windowName;
			settings.windowURL = this.href || settings.windowURL;
			
			var centeredY,centeredX;
			if(settings.centerBrowser){
				
				if ($.browser.msie) {//hacked together for IE browsers
					centeredY = (window.screenTop - 120) + ((((document.documentElement.clientHeight + 120)/2) - (settings.height/2)));
					centeredX = window.screenLeft + ((((document.body.offsetWidth + 20)/2) - (settings.width/2)));
				}else{
					centeredY = window.screenY + (((window.outerHeight/2) - (settings.height/2)));
					centeredX = window.screenX + (((window.outerWidth/2) - (settings.width/2)));
				}

				centeredY = parseInt(centeredY);
				centeredX = parseInt(centeredX);
				window.showModalDialog(settings.windowURL, window, windowFeatures + ';dialogLeft:' + centeredX + ';dialogTop:' + centeredY);
			}else if(settings.centerScreen){
				centeredY = (screen.height - settings.height)/2;
				centeredX = (screen.width - settings.width)/2;
				centeredY = parseInt(centeredY);
				centeredX = parseInt(centeredX);
				
				window.showModalDialog(settings.windowURL, window, windowFeatures + ';dialogLeft:' + centeredX + ';dialogTop:' + centeredY);
			}else{
				window.showModalDialog(settings.windowURL, window, windowFeatures + ';dialogLeft:' + settings.left + ';dialogTop:' + settings.top);
			}
			return false;
		}
		else if( settings.type == 0 )
		{
			var windowFeatures =    "";
			if( settings.height != "0" || settings.width != "0" )
			{
				windowFeatures =    'height=' + settings.height +
				',width=' + settings.width + ", ";
				//settings.centerBrowser = 0;
				//settings.centerScreen = 0;
			}
			else
			{
				settings.centerBrowser = 0;
				settings.centerScreen = 0;
			}
			
			windowFeatures +=    'toolbar=' + settings.toolbar +
								',scrollbars=' + settings.scrollbars +
								',status=' + settings.status + 
								',resizable=' + settings.resizable +
								',location=' + settings.location +
								',menuBar=' + settings.menubar;
			
				settings.windowName = this.name || settings.windowName;
				settings.windowURL = this.href || settings.windowURL;
				var centeredY,centeredX;
				if(settings.centerBrowser){
					
					if ($.browser.msie) {//hacked together for IE browsers
						centeredY = (window.screenTop - 120) + ((((document.documentElement.clientHeight + 120)/2) - (settings.height/2)));
						centeredX = window.screenLeft + ((((document.body.offsetWidth + 20)/2) - (settings.width/2)));
					}else{
						centeredY = window.screenY + (((window.outerHeight/2) - (settings.height/2)));
						centeredX = window.screenX + (((window.outerWidth/2) - (settings.width/2)));
					}
					centeredY = parseInt(centeredY);
					centeredX = parseInt(centeredX);
					winObj = window.open(settings.windowURL, settings.windowName, windowFeatures+',left=' + centeredX +',top=' + centeredY);
				}else if(settings.centerScreen){
					centeredY = (screen.height - settings.height)/2;
					centeredX = (screen.width - settings.width)/2;
					centeredY = parseInt(centeredY);
					centeredX = parseInt(centeredX);
					winObj = window.open(settings.windowURL, settings.windowName, windowFeatures+',left=' + centeredX +',top=' + centeredY);
				}else{
					winObj = window.open(settings.windowURL, settings.windowName, windowFeatures+',left=' + settings.left +',top=' + settings.top);	
				}
				return winObj;
				//return false;
		}
		else if( settings.type == 2 )
		{
			var windowFeatures =    'dialogHeight:' + settings.height + 'px' +
							';dialogWidth:' + settings.width + 'px' +
							';scroll:' + settings.scrollbars +
							';status:' + settings.status + 
							';resizable:' + settings.resizable;
			settings.windowName = this.name || settings.windowName;
			settings.windowURL = this.href || settings.windowURL;
			
			var centeredY,centeredX;
			var varParam = new Object();
			if(settings.centerBrowser){
			
				if ($.browser.msie) {//hacked together for IE browsers
					centeredY = (window.screenTop - 120) + ((((document.documentElement.clientHeight + 120)/2) - (settings.height/2)));
					centeredX = window.screenLeft + ((((document.body.offsetWidth + 20)/2) - (settings.width/2)));
				}else{
					centeredY = window.screenY + (((window.outerHeight/2) - (settings.height/2)));
					centeredX = window.screenX + (((window.outerWidth/2) - (settings.width/2)));
				}
			
				centeredY = parseInt(centeredY);
				centeredX = parseInt(centeredX);
				return window.showModalDialog(settings.windowURL, varParam, windowFeatures + ';dialogLeft:' + centeredX + ';dialogTop:' + centeredY);
			}else if(settings.centerScreen){
				centeredY = (screen.height - settings.height)/2;
				centeredX = (screen.width - settings.width)/2;
				centeredY = parseInt(centeredY);
				centeredX = parseInt(centeredX);
				
				return window.showModalDialog(settings.windowURL, varParam, windowFeatures + ';dialogLeft:' + centeredX + ';dialogTop:' + centeredY);
			}else{
				return window.showModalDialog(settings.windowURL, varParam, windowFeatures + ';dialogLeft:' + settings.left + ';dialogTop:' + settings.top);
			}
			return false;
		}
	};
})(jQuery);
