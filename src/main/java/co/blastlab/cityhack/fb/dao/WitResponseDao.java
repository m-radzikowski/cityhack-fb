package co.blastlab.cityhack.fb.dao;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WitResponseDao {

	String id;
	Double confidence;
	VALUE value;

	enum VALUE {
		NEUTRAL, POSITIVE, NEGATIVE;
	}
}
