package ch.cern.cmms.eamlightweb.tools.autocomplete;

public class WhereParameter {

	private Object value;
	private JOINER joiner;
	private OPERATOR operator = OPERATOR.STARTS_WITH;
	private Boolean forceCaseInsensitive = false;
	private Boolean upperCase = false;
	private Boolean leftParenthesis = false;
	private Boolean rightParenthesis = false;

	public WhereParameter(Object value) {
		super();
		this.value = value;
	}

	public WhereParameter(Object value, JOINER joiner) {
		super();
		this.value = value;
		this.joiner = joiner;
	}

	public WhereParameter(OPERATOR operator) {
		super();
		this.operator = operator;
	}

	public WhereParameter(OPERATOR operator, Object value) {
		super();
		this.value = value;
		this.operator = operator;
	}

	public WhereParameter(OPERATOR operator, Object value, Boolean upperCase) {
		super();
		this.value = value;
		this.operator = operator;
		this.upperCase = upperCase;
	}

	public WhereParameter(OPERATOR operator, Object value, JOINER joiner) {
		super();
		this.value = value;
		this.operator = operator;
		this.joiner = joiner;
	}

	public WhereParameter(OPERATOR operator, Object value, JOINER joiner, Boolean upperCase) {
		super();
		this.value = value;
		this.operator = operator;
		this.joiner = joiner;
		this.upperCase = upperCase;
	}

	public WhereParameter(Object value, JOINER joiner, OPERATOR operator, Boolean forceCaseInsensitive) {
		super();
		this.value = value;
		this.joiner = joiner;
		this.operator = operator;
		this.forceCaseInsensitive = forceCaseInsensitive;
	}

	public WhereParameter(Object value, JOINER joiner, OPERATOR operator, Boolean forceCaseInsensitive,
			Boolean upperCase) {
		super();
		this.value = value;
		this.joiner = joiner;
		this.operator = operator;
		this.forceCaseInsensitive = forceCaseInsensitive;
		this.upperCase = upperCase;
	}

	public WhereParameter(Object value, Boolean forceCaseInsensitive) {
		super();
		this.value = value;
		this.forceCaseInsensitive = forceCaseInsensitive;
	}

	public WhereParameter(Object value, OPERATOR operator, Boolean forceCaseInsensitive) {
		super();
		this.value = value;
		this.operator = operator;
		this.forceCaseInsensitive = forceCaseInsensitive;
	}

	public WhereParameter(Object value, JOINER joiner, Boolean forceCaseInsensitive) {
		super();
		this.value = value;
		this.joiner = joiner;
		this.forceCaseInsensitive = forceCaseInsensitive;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public JOINER getJoiner() {
		return joiner;
	}

	public void setJoiner(JOINER joiner) {
		this.joiner = joiner;
	}

	public OPERATOR getOperator() {
		return operator;
	}

	public void setOperator(OPERATOR operator) {
		this.operator = operator;
	}

	public Boolean getForceCaseInsensitive() {
		return forceCaseInsensitive;
	}

	public void setForceCaseInsensitive(Boolean forceCaseInsensitive) {
		this.forceCaseInsensitive = forceCaseInsensitive;
	}

	public Boolean getLeftParenthesis() {
		return leftParenthesis;
	}

	public void setLeftParenthesis(Boolean leftParenthesis) {
		this.leftParenthesis = leftParenthesis;
	}

	public Boolean getRightParenthesis() {
		return rightParenthesis;
	}

	public void setRightParenthesis(Boolean rightParenthesis) {
		this.rightParenthesis = rightParenthesis;
	}

	public Boolean getUpperCase() {
		return upperCase;
	}

	public void setUpperCase(Boolean upperCase) {
		this.upperCase = upperCase;
	}

	public enum JOINER {
		OR, AND;
	}

	public enum OPERATOR {
		STARTS_WITH, CONTAINS, ENDS_WITH, EQUALS, IS_EMPTY, IS_NOT_EMPTY, IN, LESS_THAN, GREATER_THAN, LESS_THAN_EQUALS, GREATER_THAN_EQUALS;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "WhereParameter [" + (value != null ? "value=" + value + ", " : "")
				+ (joiner != null ? "joiner=" + joiner + ", " : "")
				+ (operator != null ? "operator=" + operator + ", " : "")
				+ (forceCaseInsensitive != null ? "forceCaseInsensitive=" + forceCaseInsensitive : "") + "]";
	}
}
