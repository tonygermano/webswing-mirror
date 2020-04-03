package org.webswing.server.model;

import org.webswing.server.services.rest.resources.model.ApplicationInfoMsg;
import org.webswing.server.services.rest.resources.model.MetaField;
import org.webswing.server.services.rest.resources.model.VariableSetName;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModelConversionFactory {
	
	public static MetaField convertMF(org.webswing.server.common.model.meta.MetaField mf) {
		MetaField result = new MetaField();
		result.setDescription(mf.getDescription());
		result.setDiscriminator(mf.isDiscriminator());
		result.setLabel(mf.getLabel());
		result.setName(mf.getName());
		result.setPresets(mf.getPresets() != null ? Arrays.asList(mf.getPresets()) : null);
		if (mf.getTab() != null)
			result.setTab(MetaField.TabEnum.valueOf(mf.getTab().toString().toUpperCase()));
		result.setTableColumns(Optional.ofNullable(mf.getTableColumns()).map(Collection::stream)
				.orElseGet(Stream::empty).map(column -> convertMF(column)).collect(Collectors.toList()));
		if (mf.getType() != null)
			result.setType(MetaField.TypeEnum.valueOf(mf.getType().toString().toUpperCase()));
		result.setValue(mf.getValue());
		if (mf.getVariables() != null)
			result.setVariables(VariableSetName.valueOf(mf.getVariables().toString().toUpperCase()));
		return result;
	}
	
	public static org.webswing.model.s2c.ApplicationInfoMsg convertAIM(ApplicationInfoMsg aim) {
		org.webswing.model.s2c.ApplicationInfoMsg result = new org.webswing.model.s2c.ApplicationInfoMsg();
		result.setName(aim.getName());
		result.setUrl(aim.getUrl());
		result.setBase64Icon(aim.getBase64Icon());
		return result;
		
	}

}
