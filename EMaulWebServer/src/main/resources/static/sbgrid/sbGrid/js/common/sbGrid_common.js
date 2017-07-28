
	// Root로부터 그리드 JS 위치까지의 절대경로
	SBGrid.DEF.DOMAIN = '/sbgrid/sbGrid/js/';
	
	/*
		description	:	____
		func id			:	____
		since			:	____
		paramerter	:	object
		return			:	____
	*/
	function setCol(){
		var objCol = [
		              	{ "id": "col1", "type": "checkbox", 	"ref": "COMM_CD", 	"style" : "text-align:center" },
						{ "id": "col2", "type": "input", 		"ref": "COMM_CD", 		"style" : "text-align:center" },
						{ "id": "col3", "type": "output", 		"ref": "COMM_NM", 		"style" : "text-align:center" },
						{ "id": "col4", "type": "checkbox", 	"ref": "COMM_ENG_NM",	"style" : "text-align:center" },
						{ "id": "col5", "type": "inputbutton", 	"ref": "UP_COMM_CD", 	"style" : "text-align:center" },
						{ "id": "col6", "type": "inputdate", 	"ref": "CLS", 			"style" : "text-align:center" },
						{ "id": "col7", "type": "combo", 		"ref": "REF_CD", 		"style" : "text-align:center" },
					];
		return objCol;
	}


	/**
	 * 문서 에서 최초 선언되는 객체.(사용)
	 **/
	/*
		description	:	____
		func id			:	____
		since			:	____
		paramerter	:	object
		return			:	____
	*/
	function doc(){
		this.grid = new Array;
		// 그리드 객체 설정 ------------------------------------------
		this.addGrid = function(id){
							//console.log(id);
							//console.log(id.m_strTargetGrid);
							var obj = this.findGrid(id.m_strTargetGrid);
							if( obj == null) {
								var gd = new Grid(id);
								this.grid[ this.grid.length ] = gd;
								return gd;
							}else{
								return obj;
							}
						}

		this.getGrid = function(id){
							var obj = this.findGrid(id.m_strTargetGrid);
							if( obj == null)
								alertBox("[Error] no Grid with id " + id + " registered");
							return obj;
						}

		this.findGrid = function(id){
							for (var i=0 ; i < this.grid.length ; i++){
								if (this.grid[i].m_strTargetGrid == id){
									return this.grid[i];
								}
							}
							return null
						}
		return this;
	}

	/**
	 * function    : cmGridMouseOver()
	 * description : onmouseover시 이벤트 처리
	 * date : 2014.03.31
	 **/
	 /*
	cmGridMouseOver = function (objId){
		var mouseRow = objId.getMouseRow(),
			mouseCol = objId.getMouseCol(),
			fixedRow = objId.getFixedRows();
			
		if(mouseRow >= fixedRow){
			objId.setCellTooltip(mouseRow, mouseCol, objId.getTextMatrix(mouseRow , mouseCol));
		} else{
			objId.setCellTooltip(mouseRow, mouseCol, '');
		}
		return;
	}
	*/
	
	/**
	 * 그리드 객체 정의...(사용)
	 * var doc = doc; // 혹은 doc 그대로 쓸 수 있음
	 * cb = doc.addGrid("BookGridList");
	 * 과 같이 불러온다. 이미 만든 것은
	 * var cb2 = doc.getGrid("BookGridList");
	 * 과 같이 불러온다.
	 *
	 **/
	 
	 
	function Grid(id){
		console.log(id);
		this.grid;
		this.grid = id;
		this.id = id.m_strTargetGrid;
		this.action;
		this.mode;
		this.nodeSet = '';//this.grid.nodeset;
		this.sendRef = "";
		this.header = "";
		this.key	= "";
		this.align = "";
		this.type = "";
		this.isChecked = -1;
		this.tmHeader = -1;
		this.isSelected = -1;
		this.cntNodeSet = "";
		this.cntObj = "";
		this.treeUpCdCol = "";
		this.treeCdCol = "";
		this.treeChkCol = "";
		this.treeLeafCol = "";
		this.closeDepth = "";
		this.VALUE  = "";
		this.LABEL = "";
		this.option = "";
		this.sheetNum = 1;
		this.selectedRow = 0;
		//this.grid.extendlastcol = "scroll";
		
		//this.grid.addEventListener("onmouseover", 'cmGridMouseOver('+this.id+')',"","");
		
		// 변경
		this.grid.setAttribute("allowselection",'false');
		//this.grid.setAttribute("datatype", "delimeter");
		this.grid.setAttribute("colsep","▦");
		this.grid.setAttribute("rowsep","▩");
		this.grid.setAttribute("ellipsis","true");
		this.grid.setAttribute("selectfontcolor", "#000000");
		this.grid.setAttribute("tooltip" , 'true');
		
		//그리드 선택 포커스 변경
		this.grid.setSelectionMode("free");	
		this.grid.setFocusColor('#fee0bd');
		
		getHeader(this);
		
		this.getInfo = function(){
			alertBox(
				"\ngrid\t    :   "			+ this.grid
				+"\nid\t    :   "			+ this.id
				+"\naction\t    :"			+ this.action
				+"\nmode\t    :   "			+ this.mode
				+"\nref\t    :   "			+ this.sendRef
				+"\nresultRef\t    :   "	+ this.nodeSet
			);
			return this;
		}
		
		
		// 변경
		this.getId = function(){
						return this.id;
					}
	
		this.setAction = function(action){
							this.action = action;
							return this;
						}
						
		this.setMode = function(mode){
						this.mode = mode;
						return this;
					}
					
		this.setSendRef = function(sendRef){
						this.sendRef = sendRef;
						return this;
					}
					
		this.setProgress = function(progress) {
						this.progress = progress;
						return this;
					}
					
		this.setInput = function(ref){
							this.sendRef = ref;
							return this;
						}
						
		this.getNodeSet = function(){
							return this.nodeSet;
						}
						
		this.setChkBox = function(col){
							this.grid.setFixedcellcheckbox(0,col,true);
							return this;
						}
						
		this.setCntObj = function(objId){
							this.cntObj = objId;
							this.setCntNodeSet(this.getCntObj().attribute("ref"));
							return this;
						}
						
		this.getCntObj = function(){
							return eval(this.cntObj);
						}
						
		this.setCntNodeSet = function(nodeset){
							this.cntNodeSet = nodeset;
							return this;
						}
						
		this.getCntNodeSet = function(){
							return this.cntNodeSet;
						}
						
						
		//그리드 체크 설정
		this.setTreeCdCol = function (treeCdCol){
							this.treeCdCol = treeCdCol;
							return this;
		}
		this.getTreeCdCol = function (){
							return this.grid.colRef(this.treeCdCol);
		}
		this.setTreeUpCdCol = function (treeUpCdCol){
							this.treeUpCdCol = treeUpCdCol;
							return this;
		}
		this.getTreeUpCdCol = function (){
							return this.grid.colRef(this.treeUpCdCol);
		}
		this.setTreeChkCol = function (treeChkCol){
							this.treeChkCol = treeChkCol;
							return this;
		}
		this.getTreeChkCol = function (){
							return this.grid.colRef(this.treeChkCol);
		}
		this.setTreeLeafCol = function (treeLeafCol){
							this.treeLeafCol = treeLeafCol;
							return this;
		}
		this.getTreeLeafCol = function (){
							return this.grid.colRef(this.treeLeafCol);
		}
		this.setCloseDepth = function (closeDepth){
			this.closeDepth = closeDepth;
			return this;
		}
		this.getCloseDepth = function (){
			return this.closeDepth
		}
				
		this.grid.rebuild();
		return this;
	}
	



	/**
	 * function    : makeGridTree()
	 * description : 그리드 트리 설정
	 * date : 2014.03.19
	 **/
	Grid.prototype.makeGridTree = function(Col , Tree , closeDepth){
		return this.grid.makeGridTree(Col , Tree , closeDepth,"","");
	}
		
	/**
	 * function    : makeGridTree()
	 * description : 그리드 트리 설정
	 * parameter   : Col        - [Integer] Depth Level정보가 있는 컬럼 Index
	 *               Tree       - [Integer] Tree를 표시할 컬럼 Index
	 *               closeDepth - [Integer] 표시할 Depth
	 *               cntLev     - [Integer]
	 *               captionId  - [String]
	 * date		: 2014.03.19
	 **/
	Grid.prototype.makeGridTree = function( Col , Tree , closeDepth, cntLev, captionId ){

		var cnt=0;

		var gridRow       = this.grid.getRow(),
			gridTotalRow  = this.grid.getRows(),
			gridFixedRows = this.grid.getFixedRows();
		
		for(var i = gridFixedRows ; i < gridTotalRow; i+=1) {
			this.grid.m_htRowSubTotal[i] = true;
			// tree depth 지정
			if(this.grid.getTextMatrix(i, Col) == ""){
				//this.grid.outlinelevel(i) =  "3";
				this.grid.setGroupLevel(i, 3);
			}else{
				this.grid.setGroupLevel(i, (parseInt(this.grid.getTextMatrix(i, Col)) + 1));
			}
			if(cntLev != ""){
				if(this.grid.getTextMatrix(i, Col) == cntLev){
					cnt++;
				}
			}else{
				cnt++;
			}
		}
		if (Tree == "" || Tree == null){
			Tree = 0;
		}

		// 트리 설정할 컬럼
		this.grid.setGroupCol(Tree);
		
		// 트리 생성
		this.grid.setGroup("complete");
		if (closeDepth == "" || closeDepth == null){
			closeDepth = -1;
		}
	
		// Depth가 설정되어 있으면 해당 Depth까지만 Tree를 표시한다.
		if(closeDepth >= 0){
			for(var i = gridFixedRows; i < gridTotalRow; i+=1){
				if(this.grid.getTextMatrix(i, Col) >= closeDepth ){
					this.grid.setGroupCollapsed(i, true);
				}else{
					this.grid.setGroupCollapsed(i, false);
				}
			}
		}
	
	//getGridRowCntMsg(cnt, captionId);
	}
	
	/**
	 * function    : collaspredGrid()
	 * description : 그리드 트리가 생성되었을 경우 이벤트에 따라 그리드의 펼침 닫힘
	 * parameter   : depthCol  - [Integer] Depth Level정보가 있는 컬럼 Index
	 *               row       - [Integer] 펼침/닫힘 되는 로우
	 * date : 2014.03.19
	 **/
	Grid.prototype.collaspredGrid = function(depthCol, row){
		var setRow;
		var gridRow       = this.grid.getRow(),
			gridTotalRow  = this.grid.getRows(),
			gridFixedRows = this.grid.getFixedRows();
			
		if(row == "" || row < gridFixedRows || row >= gridTotalRow ){
			setRow = gridRow;
		}else{
			setRow = row;
		}
		
		var depth = this.grid.getTextMatrix(setRow, depthCol);
		var temp = "";
		
		if(row <= (gridTotalRow - gridFixedRows)-1 && changeNumber(depth) < changeNumber(this.grid.getTextMatrix(setRow+1, depthCol))){
			if(setRow == gridFixedRows-1){
				return;
			}
			
			temp = this.grid.getTextMatrix(setRow+1, depthCol);
			if(temp > depth){
				if(this.grid.isCollapsed(row) == 0){
					this.grid.setGroupCollapsed(row, true);
				}else if(this.grid.isCollapsed(row) == 1){
					this.grid.setGroupCollapsed(row, false);
				}
			}else {
				return;
			}
		}
	}
	
	/**
	 * function    : deleteRow()
	 * description : Grid 행 삭제 공통 함수
	 * parameter   : none
	 * date        : 2014.03.18
	 **/
	Grid.prototype.deleteRow = function(type){
		if(type == '' || type == 'undefined' || type == null){
			type = false;
		}
		var gridRow       = this.grid.getRow(),
			gridTotalRow  = this.grid.getRows(),
			gridFixedRows = this.grid.getFixedRows(),
			gridTotalCol  = this.grid.getCols(),
			isChecked     = this.grid.getColRef('isChecked'),
			tmHeader      = this.grid.getColRef('tmHeader');
		
		var thisGrid = this.grid,
			rowCount = 0;
		
		// grid 에 fixedRow 만 있는경우 return 한다.
		if(gridTotalRow == gridFixedRows){
			alert('end');
			return;
		}
		
		var gridStartCol = 0;
		if(this.grid.getAttribute("rowheader") == 'seq'){
			gridStartCol = 1;
		}
		
		if(type || type == 'true'){
			if(confirm("체크된 Row를 삭제하시겠습니까?")){
				type = true; 
			}else{
				return;
			}
		}
		
		// 체크박스가 있는 경우
		if(isChecked >= 0 &&  type){
			for(var i=gridTotalRow; i >= gridFixedRows ; i-=1){
				if(thisGrid.getTextMatrix(i, isChecked) == 'true'){
					if(thisGrid.getRowStatus(i) == '1' || thisGrid.getRowStatus(i) == '3' ){
						thisGrid.deleteRow(i)
					}else{
						thisGrid.addStatus(i,'delete');
						thisGrid.setCellStyle("background-color",i, gridStartCol, i, gridTotalCol, "#f9b9b1");
						thisGrid.setCellStyle("text-decoration", i, gridStartCol, i, gridTotalCol, "line-through");
					}
					rowCount++;
				}
			}
			if(rowCount == 0) {
				alert("변경할 Row를 선택하세요.");
				return this;
			}
		}else{
			if(gridRow < 0){
				alert("변경할 Row를 선택하세요.")
				return;
			}
			// 선택된 Row가 FixedRow영역일 경우 Return
			if(gridFixedRows > gridRow){
				return;
			}
			if(thisGrid.getRowStatus(gridRow) == '1' || thisGrid.getRowStatus(gridRow) == '3' ){
				thisGrid.deleteRow(gridRow);
			}else{
				thisGrid.setTextMatrix(gridRow, tmHeader, "D");
				thisGrid.addStatus(gridRow, "delete");
				thisGrid.setCellStyle("background-color",gridRow, 0, gridRow, gridTotalCol, "#f9b9b1");
				thisGrid.setCellStyle("text-decoration", gridRow, 0, gridRow, gridTotalCol, "line-through");
			}
			return this;
		}
	}
	
	/**
	 * function    : selectRow()
	 * description : -
	 * parameter   : none
	 * date        : 2014.03.18
	 **/
	Grid.prototype.selectRow = function(){
		var gridRow       = this.grid.getRow(),
			gridTotalRow  = this.grid.getRows(),
			gridFixedRows = this.grid.getFixedRows(),
			gridTotalCol  = this.grid.getCols(),
			isChecked     = this.grid.getColRef('isChecked'),
			tmHeader      = this.grid.getColRef('tmHeader'),
			where = "";
	
		// 체크된 모든 행을 찾고, 선택 해제
		for(var i=gridFixedRows; i<gridTotalRow; i+=1){
			if(this.grid.getTextMatrix(i,isChecked) == 'true'){
				this.grid.setTextMatrix(i, isChecked, 'false');
				where += i + ',';
			}
		}
		this.grid.clearStatus();

		if ( where == "" ){
			return;
		}

		where   = where.substring( 0, where.length - 1);
		
		var whereList = where.split(",");
		var lenwlist = whereList.length;
		
		if ( lenwlist < 1){
			return this;
		}
		
		for ( var i = 0; i < lenwlist; i++ )  {
			if (this.grid.getTextMatrix(i+1, tmHeader) != "I" || tmHeader == -1){
				this.grid.setTextMatrix(parseInt(whereList[i]), isChecked, "true");
				this.grid.addStatus(parseInt(whereList[i]),"insert");
			}
		}
		
		return this;
	}
	
	/**
	 * function    : selectUpdateData()
	 * description : 선택된 행에 그리드 상태정보 설정
	 * parameter   : udpate - [String] 그리드 상태 정보
	 *                      - 'I' : insert
	 *                      - 'U' : update
	 *                      - 'D' : delete
	 * date        : 2014.03.19
	 **/
	
	Grid.prototype.selectUpdateData = function(udpate){

		var selectRow="";
		var selctedRow = this.grid.selectedRows;
		for( var i = selctedRow;  i > 0; i--) {
			selectRow += this.grid.selectedRow(i-1) + ",";
		}
		
		if ( selectRow != "" ){
			selectRow = selectRow.substr(0 , selectRow.length-1);
		}else{
			return;
		}
		
		this.grid.clearStatus();
		selectRowList = selectRow.split(",");
		
		if ( selectRowList.length < 1){
			return this;
		}
		
		if (udpate== "" || udpate == null){
			udpate = "U";
		}
		
		var selectRowListLen = selectRowList.length;
		for ( var i = 0 ; i < selectRowListLen ; i+=1 ){
			this.grid.addStatus(selectRowList[i],udpate);
		}
		
		return this;
	}
	
	/**
	 * function    : changeFlag()
	 * description : 데이터가 수정, 삭제시 flag 변경
	 * parameter   : none
	 * date        : 2014.03.19
	 **/
	Grid.prototype.changeFlag = function(){
		var hds = this.header,
			chkColNum = 0,
			tmColNum  = 1,
			gridRow = this.grid.getRow();
		
		if( hds.indexOf("isChecked") > -1 && hds.indexOf("tmHeader") > -1 ){
			chkColNum = this.grid.getColRef("isChecked");
			tmColNum  = this.grid.getColRef("tmHeader");
		}else{
			if(this.grid.getAttribute("rowheader") == "seq"){
				chkColNum = 1;
				tmColNum = 2;
			}
		}
		
		var tmHeader = this.grid.getTextMatrix(gridRow, tmColNum);
		if( tmHeader=="" || tmHeader=="D"){
			if(tmHeader==""){
				this.grid.setTextMatrix(gridRow, chkColNum, 'true');
			}
			this.grid.setTextMatrix(gridRow, tmColNum, 'U');
			this.grid.addStatus(gridRow, "update");
		}
	}
	
	/**
	 * function    : deleteFlag()
	 * description : flag 삭제
	 * parameter   : none
	 * date        : 2014.03.19
	 **/
	Grid.prototype.deleteFlag = function(){
		var hds = this.header,
			chkColNum = 0,
			tmColNum  = 1,
			gridRow = this.grid.getRow();
		
		if( hds.indexOf("isChecked") > -1 && hds.indexOf("tmHeader") > -1 ){
			chkColNum = this.grid.getColRef("isChecked");
			tmColNum  = this.grid.getColRef("tmHeader");
		}else{
			if(this.grid.getAttribute("rowheader") == "seq"){
				chkColNum = 1;
				tmColNum = 2;
			}
		}
		this.grid.setTextMatrix(gridRow, chkColNum, 'false');
		this.grid.setTextMatrix(gridRow, tmColNum, '');
		this.grid.addStatus(gridRow, "");
	}
	
	/**
	 * function    : setAllChkFlagCol()
	 * description : fixedcellcheckbox flag 처리
	 * parameter   : colNm - [String] 컬럼명
	 * date        : 2014.03.19
	 **/
	Grid.prototype.setAllChkFlagCol = function(colNm){
		var hds = this.header;
		var gdCnt = 0;
		
		var gridRow       = this.grid.getRow(),
			gridTotalRow  = this.grid.getRows(),
			gridFixedRows = this.grid.getFixedRows(),
			gridTotalCol  = this.grid.getCols(),
			isChecked     = this.grid.getColRef('isChecked'),
			tmHeader      = this.grid.getColRef('tmHeader');
			
		if( hds.indexOf(colNm) > -1 ){
			var colNum = this.grid.getColRef(colNm);
			
			if(gridRow == 0 && this.grid.fixedcellcheckbox(0, colNum) == true ){
				var chkVal = this.grid.fixedcellischeck(0, colNum);
				for(var gdRow = gridFixedRows ; gdRow < gridTotalRow; gdRow+=1){
					gridRow = gdRow;
					if( chkVal == true ){
						this.changeFlag();
					}else{
						this.deleteFlag();
					}
				}
			}
		}
		return this;
	}
			
	/**
	 * function    : setAllChkFlag()
	 * description : fixedcellcheckbox flag 처리
	 * parameter   : none
	 * date        : 2014.03.19
	 **/
	Grid.prototype.setAllChkFlag = function(){
		return this.setAllChkFlagCol("isChecked");
	}
	
	
	/**
	 * function    : chkGridStatus()
	 * description : -
	 * parameter   : none
	 * date        : 2014.03.19
	 **/
	Grid.prototype.chkGridStatus = function(){
		try{
			var hds = this.header,
				gridTotalRow  = this.grid.getRows(),
				gridFixedRows = this.grid.getFixedRows();
				
			if( hds.indexOf("isChecked") > -1 && hds.indexOf("tmHeader") > -1){
				for(var gdRow = gridFixedRows; gdRow < gridTotalRow; gdRow++){
					this.checkBoxRow(gdRow);
				}
			}
		}catch(e){}
	}
			
	/**
	 * function    : setAllSelected()
	 * description : 그리드 전체 업데이트 상태 체크
	 * parameter   : colNm        - [String] 컬럼명
	 *             : exceptStatus - [String] 상태
	 * date        : 2014.03.19
	 **/
	Grid.prototype.setAllSelected = function(colNm , exceptStatus ) {
		var tmpStatus = "",
			gridTotalRow  = this.grid.getRows(),
			gridFixedRows = this.grid.getFixedRows(),
			tmHeader      = this.grid.getColRef('tmHeader');
		
		if(colNm==""){
			colNm="isChecked";
		}
		var colNmIdx = this.grid.getColRef(colNm);
		
		for(var row = gridFixedRows; row < gridTotalRow; row++){
			tmpStatus = this.grid.getTextMatrix(row, tmHeader)
			
			if( tmpStatus != "" && ("," + exceptStatus).indexOf(","+tmpStatus) > -1 ){
				continue;
			}
			this.grid.setTextMatrix(row, colNmIdx, 'true');
			this.grid.setTextMatrix(row, tmHeader, 'U');
			this.grid.addStatus(row,"update");
		}
	}
		
	//문덕찬 추가	(체크박스 변경시)
	Grid.prototype.checkBox = function(){
		this.checkBoxRow(this.grid.getRow());
	}

	/**
	 * function    : setAllSelected()
	 * description : 체크박스 변경시
	 * parameter   : row        - [Integer] 컬럼명
	 * date        : 2014.03.19
	 **/
	Grid.prototype.checkBoxRow = function(row){
	
		var chkCol = this.grid.getColRef("isChecked");
		var hdrCol = this.grid.getColRef("tmHeader");
		var grdRow = this.grid.getRow();
		var gridFixedRows = this.grid.getFixedRows();
		
		var isChecked = this.grid.getTextMatrix(row, chkCol);
		var tmHeader  = this.grid.getTextMatrix(row, hdrCol);
		
		if(isChecked == "true"){
			if(tmHeader == "D"){
				this.grid.addStatus(row, "delete");	
			}else if (tmHeader=="U"){
				this.grid.addStatus(row, "update");	
			}else if (tmHeader=="I"){
				this.grid.addStatus(row, "insert");
			}
		}else{
			if( tmHeader=="D" ||  tmHeader=="U" ) {
				this.grid.setTextMatrix(row, hdrCol, '');
				this.grid.setCellstyle("text-decoration", grdRow, 0, grdRow, 0, '');
			}else if (tmHeader=="I"){
				this.grid.deleteRow(row);
			}
		}
		
		if( grdRow != 0 && this.grid.fixedcellcheckbox(0,chkCol) == true ){
			var hdrUpdateVal = this.grid.findRow("U", gridFixedRows, hdrCol);
			var hdrInsertVal = this.grid.findRow("I", gridFixedRows, hdrCol);
			var hdrDeleteVal = this.grid.findRow("D", gridFixedRows, hdrCol);
			var hdrNonVal	 = this.grid.findRow("",  gridFixedRows, hdrCol);
			
			if( hdrNonVal < 0 && (hdrUpdateVal > 0 || hdrInsertVal > 0 || hdrDeleteVal > 0 )){
				this.grid.setFixedcellcheckbox(0, chkCol, true);
			}else if(hdrNonVal > 0 && (hdrUpdateVal > 0 || hdrInsertVal > 0 || hdrDeleteVal > 0 )){
				this.grid.setFixedcellcheckbox(0, chkCol, false);
			}
		}
	}
		
		
	/**
	 * function    : saveExcel()
	 * description : 엑셀다운로드 체크
	 * parameter   : frmId     - 폼 태그 아이디
	 *               action    - Action 경로
	 *               target    - 아이프레임 아이디
	 *               fileNm    - 파일명
	 * date        : 2014.04.04
	 **/
	Grid.prototype.saveExcel = function(frmId, action, target, fileNm){
		var docDatagrid = SBGrid.UT.XmlLib.createXML("datagrid");
		var xnDatagrid = docDatagrid.firstChild;
		
		xnDatagrid.setAttribute("caption",     this.grid.caption);
		xnDatagrid.setAttribute("dataHeight",  this.grid.dataHeight);
		xnDatagrid.setAttribute("rowHeight",   this.grid.m_nRowHeight);
		
		var xnFixedCell = SBGrid.UT.XmlLib.createChild(xnDatagrid, "fixedcell");
			xnFixedCell.setAttribute("fontFamily",      this.grid.m_objFixedCell.attribute["font-family"]);
			xnFixedCell.setAttribute("fontSize",        this.grid.m_objFixedCell.attribute["font-size"]);
			xnFixedCell.setAttribute("fontWeight",      this.grid.m_objFixedCell.attribute["font-weight"]);
			xnFixedCell.setAttribute("fontStyle",       this.grid.m_objFixedCell.attribute["font-style"]);
			xnFixedCell.setAttribute("color",           this.grid.m_objFixedCell.attribute["color"]);
			xnFixedCell.setAttribute("textAlign",       this.grid.m_objFixedCell.attribute["text-align"]);
			xnFixedCell.setAttribute("textDecoration",  this.grid.m_objFixedCell.attribute["text-decoration"]);
			xnFixedCell.setAttribute("verticalAlign",   this.grid.m_objFixedCell.attribute["vertical-align"]);
			xnFixedCell.setAttribute("backgroundColor", this.grid.m_objFixedCell.attribute["background-color"]);
			
		for (var i=this.grid.fixedCols; i<this.grid.cols; i++) {
			var objColumn = this.grid.m_arColumn[i];
			var xnCol = SBGrid.UT.XmlLib.createChild(xnDatagrid, "col");
				xnCol.setAttribute("ref", objColumn.attribute["ref"]);					// ref
				xnCol.setAttribute("width", this.grid.m_arColWidth[i]);					// width
				xnCol.setAttribute("fontFamily", objColumn.attribute["font-family"]);	// font-family
				xnCol.setAttribute("fontSize", objColumn.attribute["font-size"]);		// font-size
				xnCol.setAttribute("fontWeight", objColumn.attribute["font-weight"]);	// font-weight
				xnCol.setAttribute("fontStyle", objColumn.attribute["font-style"]);		// font-style
				xnCol.setAttribute("color", objColumn.attribute["color"]);				// color
				xnCol.setAttribute("textAlign", objColumn.attribute["text-align"]);		// text-align
				xnCol.setAttribute("borderColor", objColumn.attribute["border-color"]);	// border-color
				xnCol.setAttribute("borderWidth", objColumn.attribute["border-width"]);	// border-width
				xnCol.setAttribute("borderStyle", objColumn.attribute["border-style"]);	// border-style
		}
			
		//#######  add Data ########
		var arrCloneJsonDt = new Array();
		for ( var i = 0; i < this.grid.m_xlRefNode.length; i++) {
			arrCloneJsonDt.push(this.grid.m_xlRefNode[i]);
		}
		
		var f = $('#'+frmId);
			f.attr("action", action).attr("target", target).attr("method", "post").attr('enctype','multipart/form-data');
		
		var strDocDatagrid   = JSON.stringify(eval(this.grid.jsonref));
		var strDocDatagrid1  = SBGrid.UT.XmlLib.serializeToString(docDatagrid);
		
		$('#SBHE___SB_ExcelDownload_Name').val(fileNm);
		$('#SBHE___SB_ExcelDownload_Data').val(strDocDatagrid);
		$('#SBHE___SB_ExcelDownload_Data1').val(strDocDatagrid1);
		$('#SBHE___SB_ExcelDownload_Type').val('json');
		
		console.log('11111111 strDocDatagrid    :  '+strDocDatagrid);
		console.log('22222222 strDocDatagrid1    :  '+strDocDatagrid1);
		f.submit();
		
	}

	/**
	 * function    : fUpDownMove()
	 * description : 그리드 행 이동 함수
	 * parameter   : cls     - [String] up , down
	 * date        : 2014.04.04
	 **/
	Grid.prototype.fUpDownMove = function(cls){
		var row  = this.grid.getRow(),
			rows = this.grid.getRows(),
			cols = this.grid.getCols(),
			fixedRow = this.grid.getFixedRows(),
			g = this.grid;
		
		if(row <= 0 || row == "" || row == "undefined" || rows==1){
			return;
		}
		if(cls == "up"){
			if(row == fixedRow ){
				return;
			}
			var gridRow = row;
			
			if(gridRow > 2){
				if(gridRow >=3){
					g.selectRow(this.selectedRow);
					g.deSelectRow(this.selectedRow);
					g.row = row - 2;
					g.insertRow(g.row+1);
					for(var i = g.getFixedCols() ; i < cols ; i++){
						var cp = g.getTextMatrix(row+1, i);
						g.setTextMatrix(g.row, i, cp);
					}
					g.deleteRow(g.row + 2);
					g.row = row - 1;
					g.selectRow(g.row);
					this.selectedRow = g.row;
				}
			}else{
				g.selectRow(this.selectedRow);
				g.deSelectRow(this.selectedRow);
				g.insertRow(fixedRow);
				for(var i = g.getFixedCols() ; i < cols ; i++){
					var cp = g.getTextMatrix(row + 1, i);
					g.setTextMatrix(row-1, i, cp);
				}
				
				g.row = fixedRow;
				g.deleteRow(row + 1);
				g.selectRow(fixedRow);
				this.selectedRow = fixedRow;
			}
		}else{
			if(row == rows - 1){
				return;
			}
			g.selectRow(this.selectedRow);
			g.deSelectRow(this.selectedRow);
			g.row = row+2;
			if(g.row == rows){
				g.addRow();
			}else{
				g.insertRow(g.row);
			}
			for(var i= g.getFixedCols(); i < cols ; i+=1){
				var cp = g.getTextMatrix(row, i);
				g.setTextMatrix(g.row, i, cp);
			}
			g.deleteRow(g.row-2);
			g.selectRow(g.row-1);
			this.selectedRow = g.row-1;
			g.row = row + 1;
		}
	}
		
	/**
	 * function    : doFgCopy()
	 * description : 그리드 행 이동 함수
	 * parameter   : none
	 * date        : 2014.04.04
	 **/
	Grid.prototype.doFgCopy = function(){
		var rows = this.grid.getRows(),
			cols = this.grid.getCols(),
			fixedRows = this.grid.getFixedRows(),
			fixedCols = this.grid.getFixedCols(),
			g = this.grid;
		
		if( rows <= fixedRows){
			alert("복사 할 행을 선택해 주십시요.");
			return;
		}
		var row = g.getRow()
		if(row == -1){
			g.addRow();
			for(var i=fixedCols; i<cols; i+=1){
				var cp = g.getTextMatrix(rows-1, i);
				g.setTextMatrix(rows, i, cp);
			}
		}else{
			this.grid.insertRow(row+1);
			for(var i=fixedCols; i<cols; i+=1){
				var cp = g.getTextMatrix(row, i);
				g.setTextMatrix(row+1, i, cp);
			}
		}
	}
		
		
	Grid.prototype.getGridStatus = function(){
		return(this.grid.getGridStatus());
	}


	/**
	 * function    : __gridSizingH()
	 * description : 그리드 높이를 설정할 Height값 [ 기본값 100px ]
	 * parameter   : Height     - [Integer] 그리드 높이
	 * date        : 2014.03.24
	 **/
	__gridSizingH = function(heightP){
		if (heightP == undefined || (heightP != undefined && heightP == '')){
			heightP =100;
		}
		
		var gHeight = 0;
		gHeight = gHeight + (heightP * 1);
		
		return gHeight;
	};

	/**
	 * function    : __gridSizingW()
	 * description : 그리드 너비를 설정할 Width값 [ 기본값 100px ]
	 * parameter   : Width     - [Integer] 그리드 너비
	 * date        : 2014.03.24
	 **/
	__gridSizingW = function(widthP){
		if (widthP == undefined || (widthP != undefined && widthP == '')){
			widthP =100;
		}
		
		var gWidthP = 0;
		gWidthP = gWidthP + (widthP * 1);
		
		return gWidthP;
	};

	/**
	 * function    : getHeader()
	 * description : init 시점에 Grid Header 값을 가져온다.
	 * parameter   : -
	 * date        : 2014.03.24
	 **/
	function getHeader(me){
		try{
			var tmpHeader	= "",
				keyCol		= "",
				align		= "",
				type		= "",
				col_index	= 0;

			var gridChildLen = me.grid.children.length,
				gridChild = me.grid.children;
			
			for( var i = 0 ; i < gridChildLen; i++){
				if( gridChild.m_arOrder[i].substring(0,3) == 'col'){
					var gridChildObj = eval('gridChild.m_htCollection.'  + gridChild.m_arOrder[i]);
						tmpHeader	+= gridChildObj.getAttribute('ref')  + me.grid.getAttribute('colsep');
						align		+= gridChildObj.getAttribute('key')  + ',';
						type		+= gridChildObj.getAttribute("type") + ",";

					if (gridChildObj.getAttribute("ref") == "isChecked"){
						me.isChecked = col_index;
					}
					if (gridChildObj.getAttribute("ref") == "tmHeader")	{
						me.tmHeader  = col_index;
					}
					if (gridChildObj.getAttribute("ref") == "isSelected"){//
						me.isSelected = col_index;
					}
					col_index++;
				}
			}
			if ( me.grid.getAttribute('rowheader') == 'seq' ) {
				if (me.isChecked != -1){
					me.isChecked++;
				}
				if (me.isSelected != -1){
					me.isSelected++;
				}
				if (me.tmHeader != -1){
					me.tmHeader++;
				}
			}
			me.key    = keyCol.substring(0,keyCol.length - 1);
			me.header = tmpHeader.substr(0, tmpHeader.length - 1) + me.grid.getAttribute('rowsep');
			me.align  = align.substring(0,align.length - 1);
			me.type   = type.substring(0,type.length - 1);
			return me;
		}catch(e){
			alertBox("[getHeader(me)]::"+e);
		}
	}
	
	/**
	 * function    : insertRow()
	 * description : Grid 행 삽입 공통 함수
	 * parameter   : type - [String] 행삽입 타입 [위치]
	 *                    - 'first' : 그리드 첫번째 행에 행 삽입
	 *                    - 'last'  : 그리드 마지막행에 행 삽입
	 *                    - 'above' : 선택한 행의 윗쪽에 행 삽입   [ default : 선택한 행 아래 삽입 ]
	 * date : 2014.03.18
	 **/

	 Grid.prototype.insertRow = function(type){
		var gridRow       = this.grid.getRow(),
			gridTotalRow  = this.grid.getRows(),
			gridFixedRows = this.grid.getFixedRows(),
			gridTotalCol  = this.grid.getCols(),
			isChecked     = this.grid.getColRef('isChecked'),
			tmHeader      = this.grid.getColRef('tmHeader');
		
		var gridStartCol = 0;
		
		if(this.grid.getAttribute("rowheader") == 'seq'){
			gridStartCol = 1;
		}
		
		if(gridRow < gridFixedRows){
			gridRow = gridRow - 1;
		}
		if(type=="first"){
			if(this.grid.row==0){
				this.grid.insertRow(gridRow , "below");
			}else{
				this.grid.insertRow(gridFixedRows , "above");
			}
		}else if(type=="last"){
			this.grid.addRow();
		}else if(type=="above"){
			if(gridRow < 0){
				return false;
			}
			if(gridRow==0){
				this.grid.insertRow(gridRow , "below");
			}else{
				this.grid.insertRow(gridRow , "above");
			}
		}else{
			this.grid.insertRow(gridRow , "below");
		}
		
		// 체크박스가 있는 경우 행삽입과 동시에 체크로 설정한다.
		if ( isChecked >= 0 ){
			switch(type){
				case "first" : {
					this.grid.setTextMatrix(gridFixedRows,isChecked,'true');
					this.grid.setCellStyle("background-color", gridFixedRows, gridStartCol, gridFixedRows, gridTotalCol, "#fee0bd");
					this.grid.addStatus(gridFixedRows, "insert");
					break;
				}
				case "last" : {
					this.grid.setTextMatrix(gridTotalRow,isChecked,'true');
					this.grid.setCellStyle("background-color", gridTotalRow, gridStartCol, gridTotalRow, gridTotalCol, "#fee0bd");
					this.grid.addStatus(gridTotalRow, "insert");
					break;
				}
				case "above" : {
					this.grid.setTextMatrix(gridRow,isChecked,'true');
					this.grid.setCellStyle("background-color", gridRow, gridStartCol, gridRow, gridTotalCol, "#fee0bd");
					this.grid.addStatus(gridRow, "insert");
					break;
				}
				default : 
					this.grid.setTextMatrix((gridRow+1),isChecked,'true');
					this.grid.setCellStyle("background-color", (gridRow+1), gridStartCol, (gridRow+1), gridTotalCol, "#fee0bd");
					this.grid.addStatus((gridRow+1), "insert");
			}
		}
		
		if ( tmHeader >= 0 ){
			this.grid.setTextMatrix(gridRow,tmHeader, "I");
		}

		if(type == 'last'){
			// 해당위치에 스크롤 위치
			this.grid.setTopRow(gridTotalRow);
		}
		
		if ( tmHeader >= 0 ) {
			if(this.grid.getAttribute("rowheader") == "seq"){
				this.grid.setTextMatrix(gridRow, tmHeader, 'I');
			}else{
				this.grid.setTextMatrix(gridRow, tmHeader, 'I');
			}
		}
		this.grid.row = -1;
		return this;
	}

		/**
	 * parameter   : urlNArgs      - [String] URL 및 파라미터  ex["/ibsis/example/temp/main/popMgt.jsp?param=val1&param2=val2"
	 *               popupId       - [String] 타겟이될 팝업창
	 *               evnArgs       - [String] window.open시 들어갈 속성 ex["width=610,height=595,scrollbars=yes"]
	 * date        : 2014.03.28
	 **/
	/*
	  description	:	팝업을 생성하고 쿼리스트링으로 넘긴 값을 파라미터로 넘김
	  func id			:	cmOpenPopup()
	  since			:	____
	  paramerter	:	object
	  return			:	____
	*/
	function cmOpenPopup(urlNArgs, popupId, evnArgs){
		var ind    = urlNArgs.indexOf("?"),
			arr    = urlNArgs.split("?"),
			url    = arr[0],
			params = arr[1],
			w = "";
		if(ind != -1 && params.length != 0){
			w = window.open('about:blank', popupId, evnArgs);
			var f = $(document.createElement("form"));
			f.attr("action", url).attr("target", popupId).attr("method", "post");
			
			var argsArr = params.split("&");
			var al = argsArr.length;
			for ( var i=0; i < al; i+=1) {
				var nameNVal = argsArr[i].split("=");
				if(nameNVal[0] != "" && nameNVal[1] != ""){
					f.append($(document.createElement("input")).attr("type", "hidden").attr("name", nameNVal[0]).attr("value", nameNVal[1]));
				}
				console.log('name   :   ' + nameNVal[0] +'    value   : ' + nameNVal[1]);
			}
			$(document.body).append(f);
			f.submit();
		}else{
			w = window.open(urlNArgs, popupId, evnArgs);
		}
		return w;
	};
	
 	/*
	  description	:	____
	  func id			:	____
	  since			:	____
	  paramerter	:	object
	  return			:	____
	*/
	
	function setDefaultAttribute(arg){
		var gridAttributeInfo =
			{
				"strParentId"		: arg.parentId,
				"strId"				: arg.gridId,
				"strCaption"			: arg.caption,
				"strColWidth"		: arg.columnWidth,
				"strJsonRef"			: arg.jsonPath,
				"strStyle"				: arg.gridStyle,
				"strRowHeight"		: "25px",
				"strDataHeight"	: "25px",
			//	"strRowHeader"			: 'seq',
				"strRowHeader"			: 'update',
				"strExplorerbar"		: "sortshowmove",
			//	"strExplorerUI"			: "filteringhide",
				"strBackColorAlternate" : "#EFE0ED"
			};
		return gridAttributeInfo;
	}
	
	/**
	 sbGrid의 페이징생성 PagingRander : 각 프로젝트 화면에 맞춰서 유동적으로 변경하면서 사용
	*/
	function gfnPagingRander(areaId, nPageNum, nPageSize, nCount){
		var nGroupNumn,  //블럭 번호
        nTotalPage,      //전체 페이지
        nFromPage,       //시작 페이지
        nToPage;         //종료 페이지
        
		var nTotalPage = 0;
	    var nNaviSize = 10;  //블럭 사이즈 설정
	
	    if (nCount != 0){
	        nTotalPage  = ((nCount - 1) / nPageSize) + 1;
	        if (nPageNum > nTotalPage) {
	            nPageNum = nTotalPage;
	        }
	        
	        nGroupNum = Math.ceil((nPageNum / nNaviSize))-1;
	        nFromPage = (nGroupNum * nNaviSize)+1
	        nToPage     = (nFromPage + nNaviSize - 1);
	        if (nToPage >= nTotalPage) {
	            nToPage = nTotalPage;
	        }
	    }else{
	        nTotalPage = 0;
	    }
	    var startNum = ((nPageNum-1) * nPageSize) + 1;
		
		var pagingDiv = "";
		//console.log(nFromPage  +'######'+ nToPage+'#####'+nTotalPage+'####'+nPageNum);
		if (nTotalPage != 0 && nTotalPage != 1) {
		    pagingDiv +="\n<div class='page'>\n";
		    if (nFromPage > 1) {
		        pagingDiv += "<a class=\"page_btn first\"";
		        pagingDiv += "title=첫번째페이지 href=\"#\"  onclick=\"SBGrid_pagingInit(0)\"; return flase;\"></a>&#160;";
		        pagingDiv += "<a class=\"page_btn prev\"";
		        pagingDiv += "title=이전 10페이지 href=\"#\" onclick=\"SBGrid_pagingInit("+(nFromPage-1)+")\"; return flase;\">";
		        pagingDiv += "</a>&#160;";
		    }
		    for (var i = nFromPage; i <= nToPage; i++) {
		        if (i == nPageNum) {
		            pagingDiv += "<span class=\"page_num\"><a class=\"on\">" + i+ "</a></span>&#160;";
		        } else {
		            pagingDiv += "<span class=\"page_num\">";
		            pagingDiv += "<a href=\"javascript:SBGrid_pagingInit("+i+")\">" + i + "</a>";
		            pagingDiv += "</span>&#160;";
		        }
		    }
		    if (nToPage < nTotalPage) {
		        pagingDiv += "<a class=\"page_btn next\"";
		        pagingDiv += "title=\"다음\" href=\"#\"  onclick=\"SBGrid_pagingInit('"+ ( nToPage + 1 ) +"')\"></a>";
		        pagingDiv += "<a class=\"page_btn end\"";
		        pagingDiv += "title=\"마지막페이지\" href=\"#\" onclick=\"SBGrid_pagingInit('"+ nTotalPage +"')\">";
		        pagingDiv += "</a>&#160;";
		    }
		    pagingDiv += "</div>\n";
		} else {
		    pagingDiv += "&nbsp;";
		}
		$('#'+areaId).html(pagingDiv);
	}
	
	/**
	 * paging된 sbGrid json데이터에 총카운트기준의 순번셋팅 ( _idx 컬럼으로 사용. ) 
	 * @param gridJsonData
	 * @param nPageNum
	 * @param nPageSize
	 * @param nCount
	 * @param orderBy
	 */
	function gfnSetGridPagingIndex(gridJsonData, nPageNum, nPageSize, nCount, orderBy){
		if(orderBy == null){
			orderBy = "ASC";
		}
		
		if(!$.isNumeric(nPageNum)){
			return;
		}
		if(!$.isNumeric(nPageSize)){
			return;
		}
		if(!$.isNumeric(nCount)){
			return;
		}
		if(gridJsonData != null && gridJsonData.length == 0){
			return;
		}
		
		if(orderBy.toUpperCase() == "ASC"){
			var indexPage = (nPageNum - 1) * 10;
	        for(var i=1,j=0;i<=nPageSize;i++,j++){
	        	var idx = i+indexPage;
	        	if(nCount >= idx){
	        		gridJsonData[j]._idx = idx;                		
	        	}
	        }			
		} else if(orderBy.toUpperCase() == "DESC"){
			var indexPage = (nPageNum - 1) * 10;
			for(var i=0;i<nPageSize;i++){
				var idx = nCount-i-indexPage;
				if(idx > 0){
					gridJsonData[i]._idx = idx;					
				}
			}
		}
	}
	
	function gfnSetGridIndex(gridJsonData, orderBy){
		if(orderBy == null){
			orderBy = "ASC";
		}
		if(gridJsonData != null && gridJsonData.length == 0){
			return;
		}
		
		if(orderBy.toUpperCase() == "ASC"){
	        for(var i=1,j=0;i<=gridJsonData.length;i++,j++){
	            gridJsonData[j]._idx = i;                		
	        }			
		} else if(orderBy.toUpperCase() == "DESC"){
			for(var i=gridJsonData.length,j=0;i>=1;i--,j++){
	            gridJsonData[j]._idx = i;                		
	        }
		}
	}
