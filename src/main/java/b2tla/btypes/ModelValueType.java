package b2tla.btypes;

import b2tla.exceptions.UnificationException;

public class ModelValueType implements BType {
	private String name;

	public ModelValueType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public BType unify(BType other, ITypechecker typechecker) {
		if(!this.compare(other))
			throw new UnificationException();
		
		if (other instanceof ModelValueType) {
			if (!((ModelValueType) other).getName().equals(this.name)) {
				throw new UnificationException();
			} else
				return this;
		} else if (other instanceof UntypedType) {
			((UntypedType) other).setFollowersTo(this, typechecker);
		}
		throw new UnificationException();
	}

	public boolean isUntyped() {
		return false;
	}

	@Override
	public String toString() {
		return name;
	}

	public boolean compare(BType other) {
		if (other instanceof UntypedType)
			return true;
		if ( other instanceof ModelValueType){
			if (!((ModelValueType) other).getName().equals(this.name)) {
				return true;
			}
		}
		return false;
	}

	public String getTlaType() {
		return name;
	}

	public boolean containsIntegerType() {
		return false;
	}
}
