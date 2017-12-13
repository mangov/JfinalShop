/***
 *	
 **/

$().ready( function() {
	
	//添加商品至购物车
	$(".addCartItem").click( function() {
      var $this = $(this);
	  var id = $this.metadata().id;
		
	  var quantity = $("#quantity").val();
	  if (quantity == null) {
		quantity = 1;
	  }
	  var reg = /^[0-9]*[1-9][0-9]*$/;
	  if (!reg.test(quantity)) {
	    Modal.alert("添加购物车失败:商品数量不对");
	    return false;
	  }
      $.ajax({
    	type: "POST",
		url: JFinalshop.base + "/shop/cartItem/ajaxAdd",
		data: {"id": id, "quantity": quantity},
		dataType: "json",
		beforeSend: function() {
		  $this.attr("disabled", true);
		},
		success: function(data) {
		  if (data.status == "success") {
			Modal.alert("添加成功<br>购物车共计商品：" + data.totalQuantity + "件，总计金额：" + data.totalPrice);
		  } else if (data.status == "error") {
			Modal.alert(data.message);
		  }
		  $this.attr("disabled", false);
		}
	  });
	});
	
	// 产品收藏
	$(".addFavorite").click( function() {
      var $this = $(this);
	  if ($.cookie("loginMemberUsername") == null) {
	    $.flushHeaderInfo();
	    $("#member-login-modal").modal();
		return false; 
	  } else {
		var id = $(this).metadata().id;
		$.ajax({
		  url: JFinalshop.base + "/shop/favorite/ajaxAdd",
		  data: {"id": id},
		  dataType: "json",
		  beforeSend: function() {
		    $this.attr("disabled", true);
		  },
		  success: function(data) {
			if (data.status == "error") {
			  $.flushHeaderInfo();
			  $("#member-login-modal").modal();
			}
			Modal.alert(data.message);
			$this.attr("disabled", false);
		  },
		  error: function(data) {
			if ($.cookie("loginMemberUsername") == null) {
			  $.flushHeaderInfo();
			  $("#member-login-modal").modal();
			  return false;
			}
			$this.attr("disabled", false);
		  }
		});
	  }
	});
	
	$productListForm = $("#productListForm");
	$pageNumber = $("#pageNumber");
	$pageSize = $("#pageSize");
	$orderType = $("#orderType");
	$viewType = $("#viewType");
	$tableType = $("#tableType");
	$pictureType = $("#pictureType");
	
	// 每页显示数
	$pageSize.change( function() {
		$pageNumber.val("1");
		$productListForm.submit();
		return false;
	});
	
	// 商品排序
	$orderType.change( function() {
		$pageNumber.val("1");
		$productListForm.submit();
		return false;
	});
	
	// 列表方式查看
	$tableType.click( function() {
		$viewType.val("tableType");
		$productListForm.submit();
		return false;
	});
	
	// 图片方式查看
	$pictureType.click( function() {
		$viewType.val("pictureType");
		$productListForm.submit();
		return false;
	});
	
	// 添加商品浏览记录
	var maxProductHistoryListCount = 5; // 最大商品浏览记录数
	$.addProductHistory = function(name, htmlFilePath) {
	  var productHistory = {
		name: name,
		htmlFilePath: htmlFilePath
	  };
	  var productHistoryArray = new Array();
	  var productHistoryListCookie = $.cookie("productHistoryList");
	  if(productHistoryListCookie) {
		productHistoryArray = eval(productHistoryListCookie);
	  }
	  var productHistoryListHtml = "";
	  for (var i in productHistoryArray) {
		productHistoryListHtml += '<tr><td><a href="' + productHistoryArray[i].htmlFilePath + '">' + productHistoryArray[i].name + '</a></td></tr>';
	  }
	  for (var i in productHistoryArray) {
		if(productHistoryArray[i].htmlFilePath == htmlFilePath) {
		  return;
		}
	  }
	  if(productHistoryArray.length >= maxProductHistoryListCount) {
		productHistoryArray.shift();
	  }
	  productHistoryArray.push(productHistory);
	  var newProductHistoryCookieString = "";
	  for (var i in productHistoryArray) {
		newProductHistoryCookieString += ',{name: "' + productHistoryArray[i].name + '", htmlFilePath: "' + productHistoryArray[i].htmlFilePath + '"}'
	  }
	  newProductHistoryCookieString = "[" + newProductHistoryCookieString.substring(1, newProductHistoryCookieString.length) + "]";
	  $.cookie("productHistoryList", newProductHistoryCookieString, {path: "/"});
	}
	
	// 商品浏览记录列表
	var productHistoryArray = new Array();
	var productHistoryListCookie = $.cookie("productHistoryList");
	if(productHistoryListCookie) {
		productHistoryArray = eval(productHistoryListCookie);
	}
	var productHistoryListHtml = "";
	for (var i in productHistoryArray) {
		productHistoryListHtml += '<tr><td><a href="' + productHistoryArray[i].htmlFilePath + '">' + productHistoryArray[i].name + '</a></td></tr>';
	}
	$("#productHistoryListDetail").html(productHistoryListHtml);

});