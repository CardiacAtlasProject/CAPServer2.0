<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	boolean model = request.getParameter("model") != null;
%>
<div data-role="page" id="DicomDowloadDialog">
	<div data-role="header">
		<h1>Download</h1>
	</div>
	<div data-role="content">
		<div id="dicomdownloadrequestArea">
			<div class="ui-field-contain">
				<label for="selectDicoms" style="width: 11em">DICOMS Image files</label> <input type="checkbox" data-role="flipswitch" name="selectDicoms" id="selectDicoms"
					<%if (!model) {%> checked <%}%>>
			</div>
			<div class="ui-field-contain">
				<label for="selectMeta" style="width: 11em">Metadata files </label> <input type="checkbox" data-role="flipswitch" name="selectMeta" id="selectMeta"
					<%if (!model) {%> checked <%}%>>
			</div>
			<div class="ui-field-contain" data-type="horizontal">
				<label for="selectMetaPattern" style="width: 11em">Metadata File Pattern </label> <input data-mini="true" type="text" name="selectMetaPattern"
					id="metaPattern" placeholder="File name pattern" value="">
			</div>

			<div class="ui-field-contain">
				<label for="selectModelEXs" style="width: 11em">Model EX files</label> <input type="checkbox" data-role="flipswitch" name="selectModelEXs"
					id="selectModelEXs" <%if (model) {%> checked <%}%>>
			</div>
			<div class="ui-field-contain">
				<label for="selectModelVTPs" style="width: 11em">Model VTP files</label> <input type="checkbox" data-role="flipswitch" name="selectModelVTPs"
					id="selectModelVTPs">
			</div>
			<div class="ui-field-contain">
				<label for="selectModelMeta" style="width: 11em">Model Metadata</label> <input type="checkbox" data-role="flipswitch" name="selectModelMeta"
					id="selectModelMeta">
			</div>
			<input type="button" value="Submit" class="ui-btn" id="initiatedicomdownload" <%if (!model) {%> onclick="sendDICOMDownloadRequest(this);" <%} else {%>
				onclick="sendModelDownloadRequest(this);" <%}%>>
		</div>
		<div id="dicomdowloadresult" style="display: none">
			<div class="ui-field-contain">
				<label for="dicomdownloadurl">Data URL :</label> <input type="text" name="dicomdownloadurl" id="dicomdownloadurl" value="">
			</div>
			<button type="submit" id="dicomdownloadurlform" value="" onclick="openNewWindow(this);">Download Now</button>
		</div>
	</div>
</div>