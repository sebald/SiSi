package de.freiburg.uni.iig.sisi.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.freiburg.uni.iig.sisi.model.resource.Role;
import de.freiburg.uni.iig.sisi.model.resource.Subject;

public class SubjectTest {

	@Test
	public void testHasCompatibleRoles() {
		Role r1 = new Role("r1", "Role1");
		Role r2 = new Role("r2", "Role2");
		
		Subject s1 = new Subject("s1", "Name1");
		s1.addRole(r1);
		
		Subject s2 = new Subject("s2", "Name2");
		s2.addRole(r1);
		s2.addRole(r2);
		
		assertEquals("Is sublist", true, s1.hasCompatibleRoles(s2));
		assertEquals("Is not sublist", false, s2.hasCompatibleRoles(s1));
	}

}
