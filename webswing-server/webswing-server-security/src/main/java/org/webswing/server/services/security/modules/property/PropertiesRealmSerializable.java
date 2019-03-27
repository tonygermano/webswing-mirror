package org.webswing.server.services.security.modules.property;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.authz.SimpleRole;
import org.apache.shiro.realm.text.PropertiesRealm;

public class PropertiesRealmSerializable extends PropertiesRealm implements Serializable {

	private static final long serialVersionUID = -5279888126504918390L;

	private void readObject(ObjectInputStream aStream) throws IOException, ClassNotFoundException {
		aStream.defaultReadObject();

		Map<String, SimpleAccount> streamUsers = (Map) aStream.readObject();
		users.putAll(streamUsers);
		
		Map<String, SimpleRole> streamRoles = (Map) aStream.readObject();
		roles.putAll(streamRoles);
		
		setUserDefinitions((String) aStream.readObject());
		setRoleDefinitions((String) aStream.readObject());
		
		setResourcePath((String) aStream.readObject());
	}

	/**
	 * Custom serialization is needed.
	 */
	private void writeObject(ObjectOutputStream aStream) throws IOException {
		aStream.defaultWriteObject();
		aStream.writeObject(users);
		aStream.writeObject(roles);
		aStream.writeObject(getUserDefinitions());
		aStream.writeObject(getRoleDefinitions());
		aStream.writeObject(resourcePath);
	}

}
