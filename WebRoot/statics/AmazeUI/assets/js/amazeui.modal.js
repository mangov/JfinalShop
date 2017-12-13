/* AmazeUI 模态框封装  调用：Modal.alert();Modal.confirm();*/
$(function() {
  /** 模态窗口 */
  window.Modal = {
    tpls:{
	  alert:'<div class="am-modal am-modal-alert" tabindex="-1"><div class="am-modal-dialog"><div class="am-modal-bd message-text"></div><div class="am-modal-footer"><span class="am-modal-btn">确定</span></div></div></div>',
	  confirm:'<div class="am-modal am-modal-confirm" tabindex="-1" ><div class="am-modal-dialog"><div class="am-modal-bd message-text"></div><div class="am-modal-footer"><span class="am-modal-btn btn-cancel" data-am-modal-cancel>取消</span><span class="am-modal-btn btn-confirm" data-am-modal-confirm>确定</span></div></div></div>',
	  loading:'<div class="am-modal am-modal-loading am-modal-no-btn" tabindex="-1"><div class="am-modal-dialog"><div class="am-modal-hd message-text">正在载入...</div><div class="am-modal-bd"><span class="am-icon-spinner am-icon-spin"></span></div></div></div>'
	},
	register:function(events){
	  events = events || [];
	  if(events.length == 0){
	    events = ['alert', 'confirm', 'loading'];
	  }
	  var body = $(document.body);
	  this.modal = {};
	  for(var i=0; i<events.length; i++){
	    var event = events[i];
	    var tpl = this.tpls[event];
	    if(tpl){
	      this.modal[event] = $(tpl);
	      body.append(this.modal[event]);
	    }
	  }
	},
	alert:function(message, config){
	  config = config || {};
	  var confirmText = config.confirmText || "确定";
	  this.modal.alert.find(".am-modal-btn").text(confirmText);
	  this.modal.alert.find(".message-text").html(message);
	  this.modal.alert.modal("open");
	},
	confirm:function(message, config){
	  config = config || {};
	  var confirmText = config.confirmText || "确定";
	  var cancelText = config.cancelText || "取消";
	  this.modal.confirm.find(".btn-confirm").text(confirmText);
	  this.modal.confirm.find(".btn-cancel").text(cancelText);
	  this.modal.confirm.find(".message-text").html(message);
	  this.modal.confirm.modal(config).modal("open");
	},
	loading:function(message){
	  this.modal.loading.find(".message-text").html(message);
	  this.modal.loading.modal("open");
	},
	dismissLoading:function(){
	  this.modal.loading.find(".message-text").html("");
	  this.modal.loading.modal("close");
	}
  };
  Modal.register();
});