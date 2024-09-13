package org.zerock.myapp.domain;

import java.io.Serial;
import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import org.zerock.myapp.listener.CommonEntityLifecycleListener;

import lombok.Data;


@Data


@EntityListeners(CommonEntityLifecycleListener.class)
@Entity(name = "Member")
public class Member implements Serializable {
	@Serial private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "SEQ_MEMBER")
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "member_id")
	private Long id;
	
	private String name;
	
//	@ManyToOne( optional = true, targetEntity =  Team.class)
	@ManyToOne( 
			optional = true, 
			targetEntity =  Team.class, 
			fetch = FetchType.EAGER, 
			cascade = CascadeType.ALL)
	
	@JoinColumn(
			name = "my_team", 
			referencedColumnName = "team_id",
			insertable = false, 
			updatable = false)
	private Team team;
} // end class
