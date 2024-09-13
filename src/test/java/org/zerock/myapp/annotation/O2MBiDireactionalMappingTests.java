package org.zerock.myapp.annotation;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;
import org.zerock.myapp.domain.Member;
import org.zerock.myapp.domain.Team;
import org.zerock.myapp.util.PersistenceUnits;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class O2MBiDireactionalMappingTests {
	private EntityManagerFactory emf;
	private EntityManager em;
	@BeforeAll
	void beforeAll() {
		log.trace("beforeAll() invoked.");
		
		this.emf= Persistence.createEntityManagerFactory(PersistenceUnits.MYSQL);
		assertNotNull(this.emf);
		
		this.em = this.emf.createEntityManager();
		assert this.em != null;
	} // breforeAll
	
	@AfterAll
	void afterAll() {
		log.trace("afterAll() invoked.");
		
		this.em.clear();
		try { this.em.close(); } catch (Exception _ignored) {}
		try { this.emf.close(); } catch (Exception _ignored) {}
	} // afterAll
	
//	@Disabled
	@Order(1)
	@Test
//	@RepeatedTest(1)
	@DisplayName("1. prepareData")
	@Timeout(value = 5L, unit = TimeUnit.MINUTES)
	void prepareData() {
		log.trace("contextLoad() invoked.");
		try {
			this.em.getTransaction().begin();
			IntStream.of(1, 2, 3).forEachOrdered(seq -> {
				Team transientTeam = new Team();
				transientTeam.setName("TEAM_" + seq);
				
				this.em.persist(transientTeam);
			}); //.forEachOrdered
			
			Team team1 = this.em.<Team>find(Team.class, 1L);
			Team team2 = this.em.<Team>find(Team.class, 2L);
			Team team3 = this.em.<Team>find(Team.class, 3L);
			
			Objects.nonNull(team1);
			Objects.nonNull(team2);
			Objects.nonNull(team3);
			
			IntStream.rangeClosed(1, 20).forEach(seq -> {
				Member transientMember = new Member();
				transientMember.setName("NAME_" + seq);
				
				if(seq >= 15) 		team3.addMember(transientMember);
				else if(seq >= 8) 	team2.addMember(transientMember);
				else 				team1.addMember(transientMember);
				
				this.em.persist(transientMember);
			});
			
			this.em.getTransaction().commit();
		}catch(Exception e) {
			this.em.getTransaction().rollback();
			throw e;
		} // try-catch
	} // prepareData
	
//	@Disabled
	@Order(2)
	@Test
//	@RepeatedTest(1)
	@DisplayName("2. testOneToManyUniObjectGraphTraverse")
	@Timeout(value = 5L, unit = TimeUnit.MINUTES)
	void testOneToManyUniObjectGraphTraverse() {
		log.trace("testOneToManyUniObjectGraphTraverse() invoked.");

		Team foundTeam3 = this.em.<Team>find(Team.class, 3L);
		Team foundTeam2 = this.em.<Team>find(Team.class, 2L);
		Team foundTeam1 = this.em.<Team>find(Team.class, 1L);
		
		Objects.requireNonNull(foundTeam3);
		Objects.requireNonNull(foundTeam2);
		Objects.requireNonNull(foundTeam1);
		
		assert foundTeam3 != null;
		foundTeam3.getMembers().forEach(m -> log.info(m.toString()));
		
		assert foundTeam2 != null;
		foundTeam2.getMembers().forEach(m -> log.info(m.toString()));
		
		assert foundTeam1 != null;
		foundTeam1.getMembers().forEach(m -> log.info(m.toString()));
		
		
		log.info("\t+ foundTeam3 : {}", foundTeam3);
	} // testOneToManyUniObjectGraphTraverse
	
//	@Disabled
	@Order(3)
	@Test
//	@RepeatedTest(1)
	@DisplayName("3. testOneToManyBiObjectGraphTraverseFromMemberToTeam")
	@Timeout(value = 5L, unit = TimeUnit.MINUTES)
	void testObjectGraphTraverseFromMemberToTeam() {
		log.trace("testOneToManyBiObjectGraphTraverseFromMemberToTeam() invoked.");
		
		LongStream.rangeClosed(1, 20).forEach(seq -> {
			Member foundMember = this.em.<Member>find(Member.class, seq);
			
			assertNotNull(foundMember);
			log.info("\t+ foundMember : {}", foundMember);
		});
	} // testOneToManyBiObjectGraphTraverseFromMemberToTeam
} // end class
