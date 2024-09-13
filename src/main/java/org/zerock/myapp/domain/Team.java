package org.zerock.myapp.domain;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.zerock.myapp.listener.CommonEntityLifecycleListener;

import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;


@Data
@Slf4j

@EntityListeners(CommonEntityLifecycleListener.class)
@Entity(name = "Team")
@Table(name= "team")
public class Team implements Serializable { // Parent, One(1)
	@Serial private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "team_id")
	private Long id;
	
	@Basic(optional = false)
	private String name;

//	@OneToMany(targetEntity = Member.class, cascade = CascadeType.ALL)
	@OneToMany(targetEntity = Member.class)
	@JoinColumn(name = "my_team", referencedColumnName = "team_id")
	@ToString.Exclude
	private List<Member> members = new ArrayList<>();
	
	public void addMember(Member newMember) {
		log.trace("addMember({}) invoked.", newMember);
		
		this.members.add(newMember);
		newMember.setTeam(this);
	} // addMember
	
} // end class

