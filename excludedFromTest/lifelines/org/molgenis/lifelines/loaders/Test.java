package org.molgenis.lifelines.loaders;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.log4j.PropertyConfigurator;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;

public class Test {
	public static void main(String[] args) throws DatabaseException {
		PropertyConfigurator.configure("log4j.properties");  
		
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("molgenis");
		EntityManager em = emf.createEntityManager();
		
//		String name = em.find(Investigation.class, 1).getName();
//		System.out.println(name);
		
		
		
		
	}
}