package ch.cern.cmms.eamlightweb.tools;

public class Pair {

	private String code;
	private String desc;

	public Pair() {}

	public Pair(String code, String desc) {
		super();
		this.code = code;
		this.desc = desc;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDesc() {
		if (desc == null) {
			return code;
		}
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	@Override
	public String toString() {
		return "Pair [" + (code != null ? "code=" + code + ", " : "") + (desc != null ? "desc=" + desc : "") + "]";
	}

}
