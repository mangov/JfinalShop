$(function() {
	//<form class="validator-form"></form>即包含validator-form样式的表单一下验证才生效
	$('form.validator-form').validator({
	    onValid : function(validity) {
	    	//closest() 方法获得匹配选择器的第一个祖先元素，从当前元素开始沿 DOM 树向上。
	    	//find() 方法获得当前元素集合中每个元素的后代，通过选择器、jQuery 对象或元素来筛选。
		    $(validity.field).closest('.am-form-group div').find('.am-alert').hide();
		    $(validity.field).closest('.am-form-group div').find('.am-text-warning').hide();
	    },

	    onInValid : function(validity) {
		    var $field = $(validity.field);
		    var $group = $field.closest('.am-form-group div');
		    var $alert = $group.find('.am-alert');
		    // 使用自定义的提示信息 或 插件内置的提示信息
		    var msg = $field.data('validationMessage') || this.getValidationMessage(validity);

		    if (!$alert.length) {
			    $alert = $('<div class="am-alert am-alert-danger"></div>').hide().appendTo($group);
		    }

		    $alert.html(msg).show();
	    }
	});
	
	//TODO:电话和手机只用填写一个，还有点需要通过获取失去焦点及时的验证，有更好的办法再改进
	$('.js-pattern-mobile').change(function() {
	    var $field = $(this).closest('form').find('.js-pattern-phone');
	    if($(this).val()==''){
	      $field.attr("required","required");
	      if($field.val()!=''){
	        $(this).removeAttr("required");
	      }
	    }else{
	      $field.removeAttr("required");
	    }
	    $field.focus();
	    $(this).focus();
  });
	
	$('.js-pattern-phone').change(function() {
	  var $field = $(this).closest('form').find('.js-pattern-mobile');
    if($(this).val()==''){
      $field.attr("required","required");
      if($field.val()!=''){
        $(this).removeAttr("required");
      }
    }else{
      $field.removeAttr("required");
    }
    $field.focus();
    $(this).focus();
  });

});

if ($.AMUI && $.AMUI.validator) {
  // 增加多个正则
  $.AMUI.validator.patterns = $.extend($.AMUI.validator.patterns, {
    username: /^[0-9a-z_A-Z\u4e00-\u9fa5]*$/,
    alphaint: /^[a-zA-Z]*$/,
    zipCode: /^\d{6}$/,
    phone: /^(\d{3}-|\d{4}-)(\d{8}|\d{7})$/,
    mobile: /^1((3|5|8){1}\d{1}|70)\d{8}$/
  });
}