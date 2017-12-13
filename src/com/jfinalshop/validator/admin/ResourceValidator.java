package com.jfinalshop.validator.admin;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;
import com.jfinalshop.model.Resource;

public class ResourceValidator extends Validator{

	@Override
	protected void validate(Controller c) {
		validateRequiredString("resource.name", "nameMessages", "资源名称不允许为空!");
		validateRequiredString("resource.value", "valueMessages", "资源值不允许为空!");
		validateInteger("resource.orderList", 0, 1000, "orderListMessages", "排序必须为零或正整数!");

		// 检查角色名是否存在
		String name = c.getPara("resource.name","");
		String id = c.getPara("resource.id","");
		if (!Resource.dao.checkName(name, id)) {////TODO:SUN.AO 添加id 修改时排除自身
			addError("nameMessages","资源名称已存在!");
		}
		
		// 检查角色值是否存在
		String value = c.getPara("resource.value","");
		if (!Resource.dao.checkValue(value, id)) {
			addError("valueMessages","资源值已存在!");
		}
	}

	@Override
	protected void handleError(Controller c) {
		c.keepModel(Resource.class);
		c.render("/admin/resource_input.html");
	}

}
