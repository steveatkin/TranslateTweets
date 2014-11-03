<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.ResourceBundle" %>

<% 
ResourceBundle res = ResourceBundle.getBundle("TweetTranslate", request.getLocale());									
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><%=res.getString("title")%></title>
    
    <link href="css/bootstrap.min.css" rel="stylesheet">
    <link href="css/bootstrap-table.min.css" rel="stylesheet">
    <link href="css/grid.css" rel="stylesheet">
    <link href="css/tweettranslate.css" rel="stylesheet">
</head>

<body>
    <script src="js/jquery-1.11.1.min.js"></script>
    <script src="js/bootstrap.min.js"></script>
    <script src="js/bootstrap-table.min.js"></script>
    <script>
    	var translation = "";
    
    	$(document).ready(function() {
  			$(".dropdown-menu li a").click(function(event){
      			var $target = $(event.currentTarget);
      			translation = $target.attr('data-value');
      		
      			$target.closest('.btn-group')
      				.find('[data-bind="label"]').text($target.text())
         				.end()
      				.children('.dropdown-toggle').dropdown('toggle');
				});
		});
    
      	function setupEventSource() {
        	if (typeof(EventSource) !== "undefined") {
        		// Remove all the entries from the table
            	$('#tweets tbody').empty();
            	$('#tweets').bootstrapTable('load', []);
           
            	var search = $('#search').val();
          		var source = new EventSource("Tweet?search=" + search + 
          			"&translate=" + translation);
                  
          		source.onmessage = function(event) {
            		var tweet = JSON.parse(event.data);
            		$('#tweets').bootstrapTable('append', [tweet]);
          		};
          
          		source.addEventListener('finished', function(event) {
            		source.close();
          		}, false);
        	} 
        	else {
          		alert('<%=res.getString("error")%>');
        	}
        	return false;
      	}  
	</script>

    <div class="container">
   	<div class="page-header">
    <h1><%=res.getString("header")%></h1>
    </div>
      
     <div class="panel panel-default">
     	<form class="panel-body">
      	<div class="form-group">
      		<label for="search"><%=res.getString("search_topic")%></label>
      		<input type="text" class="form-control" id="search" name="message" />
      	
      		<div class="btn-group btn-input clearfix">
      		<label for="menu"><%=res.getString("translate")%></label>
      		<div id="menu" class="dropdown">
  			<button class="btn btn-default dropdown-toggle form-control" type="button" id="dropdownMenu1" data-toggle="dropdown">
    			<span data-bind="label"><%=res.getString("select")%></span> 
    			<span class="caret"></span>
  			</button>
  			<ul class="dropdown-menu" role="menu">
    		<li role="presentation"><a role="menuitem" tabindex="-1" href="#" data-value="mt-enus-eses"><%=res.getString("english_spanish")%></a></li>
    		<li role="presentation"><a role="menuitem" tabindex="-1" href="#" data-value="mt-enus-frfr"><%=res.getString("english_french")%></a></li>
  			</ul>
  			</div>
			</div>
      	</div>
      	<input type="button" id="sendID" class="btn btn-primary" value='<%=res.getString("button")%>' onclick="setupEventSource()"/>
      	</form>
	</div>
      	 
     <h3><%=res.getString("tweets")%></h3>
     	<table data-toggle="table" id="tweets">
     		<thead>
            <tr>
                <th data-field="screenName"><%=res.getString("screen_name_table")%></th>
                <th data-field="message"><%=res.getString("message_table")%></th>
                <th data-field="translation"><%=res.getString("translation_table")%></th>
            </tr>
        	</thead>
        	<tbody>
        	</tbody>
      	</table>
    </div>
    <hr/>     
</body> 
</html>      