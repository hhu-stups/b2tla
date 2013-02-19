package b2tla.btypes;

import b2tla.exceptions.UnificationException;

public class PairType extends AbstractHasFollowers {

	private BType first;
	private BType second;

	public BType getFirst() {
		return this.first;
	}

	public BType getSecond() {
		return this.second;
	}

	public void setFirst(BType newType) {
		this.first = newType;
		if (newType instanceof AbstractHasFollowers) {
			((AbstractHasFollowers) newType).addFollower(this);
		}
	}

	public void setSecond(BType newType) {
		this.second = newType;
		if (newType instanceof AbstractHasFollowers) {
			((AbstractHasFollowers) newType).addFollower(this);
		}
	}

	public void update(BType oldType, BType newType) {
		if (this.first == oldType)
			setFirst(newType);
		if (this.second == oldType)
			setSecond(newType);
	}

	public PairType(BType first, BType second) {
		setFirst(first);
		setSecond(second);
	}

	public BType unify(BType other, ITypechecker typechecker) {
		if(!this.compare(other) || this.contains(other))
			throw new UnificationException();
		
		if (other instanceof PairType) {
			((PairType) other).setFollowersTo(this, typechecker);
			setFirst(first.unify(((PairType) other).first, typechecker));
			setSecond(second.unify(((PairType) other).second, typechecker));
			return this;
		} else if (other instanceof UntypedType) {
			((UntypedType) other).setFollowersTo(this, typechecker);
			return this;
		}
		throw new UnificationException();
	}

	@Override
	public String toString() {
		String res = "";
		if (first instanceof PairType) {
			res += "(" + first + ")";
		} else
			res += first;
		res += "*";
		if (second instanceof PairType) {
			res += "(" + second + ")";
		} else
			res += second;
		return res;
	}

	public boolean isUntyped() {
		if (first.isUntyped() || second.isUntyped())
			return true;
		else
			return false;
	}

	public boolean compare(BType other) {
		if (other instanceof UntypedType)
			return true;
		if (other instanceof PairType) {
			return this.first.compare(((PairType) other).first)
					&& this.second.compare(((PairType) other).second);
		}
		return false;
	}

	@Override
	public boolean contains(BType other) {
		if(this.first.equals(other)|| this.second.equals(other)){
			return true;
		}
		if(first instanceof AbstractHasFollowers){
			if(((AbstractHasFollowers) first).contains(other))
				return true;
		}
		if(second instanceof AbstractHasFollowers){
			if(((AbstractHasFollowers) second).contains(other))
				return true;
		}
		return false;
	}

	public String getTlaType() {
		String res = first + "*";
		if (second instanceof PairType) {
			res += "(" + second + ")";
		} else
			res += second;
		return res;
	}

	public boolean containsIntegerType() {
		return this.first.containsIntegerType() || this.second.containsIntegerType();
	}

}
