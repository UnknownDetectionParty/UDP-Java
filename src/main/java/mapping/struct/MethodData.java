package mapping.struct;

/**
 * Method data wrapper.
 * 
 * @author bloo
 * @since 8/13/2017
 */
public class MethodData {
	public String owner, name, desc;

	public MethodData(String owner, String name, String desc) {
		this.owner = owner;
		this.name = name;
		this.desc = desc;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		MethodData that = (MethodData) o;

		if (owner != null ? !owner.equals(that.owner) : that.owner != null)
			return false;
		if (name != null ? !name.equals(that.name) : that.name != null)
			return false;
		return desc != null ? desc.equals(that.desc) : that.desc == null;
	}

	@Override
	public int hashCode() {
		int result = owner != null ? owner.hashCode() : 0;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (desc != null ? desc.hashCode() : 0);
		return result;
	}
}