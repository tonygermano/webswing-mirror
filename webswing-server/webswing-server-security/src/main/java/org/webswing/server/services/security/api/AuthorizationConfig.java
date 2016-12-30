package org.webswing.server.services.security.api;

import java.util.ArrayList;
import java.util.List;

import org.webswing.server.common.model.Config;
import org.webswing.server.common.model.meta.ConfigField;
import org.webswing.server.common.model.meta.ConfigFieldDefaultValueObject;
import org.webswing.server.common.model.meta.ConfigFieldOrder;

@ConfigFieldOrder({ "users", "roles" })
public interface AuthorizationConfig extends Config {

	@ConfigField(label = "Authorized Users", description = "List of authorized user ids.")
	@ConfigFieldDefaultValueObject(ArrayList.class)
	List<String> getUsers();

	@ConfigField(label = "Authorized Roles", description = "List of authorized roles.")
	@ConfigFieldDefaultValueObject(ArrayList.class)
	List<String> getRoles();
}
