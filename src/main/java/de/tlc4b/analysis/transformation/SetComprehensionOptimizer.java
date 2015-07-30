package de.tlc4b.analysis.transformation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.be4.classicalb.core.parser.Utils;
import de.be4.classicalb.core.parser.analysis.DepthFirstAdapter;
import de.be4.classicalb.core.parser.node.AComprehensionSetExpression;
import de.be4.classicalb.core.parser.node.AConjunctPredicate;
import de.be4.classicalb.core.parser.node.ACoupleExpression;
import de.be4.classicalb.core.parser.node.ADomainExpression;
import de.be4.classicalb.core.parser.node.AEqualPredicate;
import de.be4.classicalb.core.parser.node.AEventBComprehensionSetExpression;
import de.be4.classicalb.core.parser.node.AIdentifierExpression;
import de.be4.classicalb.core.parser.node.AMemberPredicate;
import de.be4.classicalb.core.parser.node.Node;
import de.be4.classicalb.core.parser.node.PExpression;
import de.be4.classicalb.core.parser.node.PPredicate;
import de.be4.classicalb.core.parser.node.Start;

/**
 * This class performs an AST transformation on set comprehension nodes. For
 * example the expression {a,b| a = b & b : 1..10} will be replaced by the
 * Event-B set comprehension {b. b : 1..10 | b |-> b}. Moreover, if the parent
 * of a set comprehension is a domain expression, this will also be used for the
 * optimization, e.g. {a,b| a = b + 1 & b : 1..10} will be replaced by {b. b :
 * 1..10 | b + 1}
 * 
 * @author hansen
 * 
 */
public class SetComprehensionOptimizer extends DepthFirstAdapter {

	/**
	 * The method called by the translator.
	 * 
	 * @param start
	 */
	public static void optimizeSetComprehensions(Start start) {
		SetComprehensionOptimizer optimizer = new SetComprehensionOptimizer();
		start.apply(optimizer);
	}

	@Override
	public void caseAComprehensionSetExpression(AComprehensionSetExpression node) {

		final LinkedList<PExpression> identifiers = node.getIdentifiers();
		final ArrayList<String> list = new ArrayList<String>();
		final Hashtable<String, AIdentifierExpression> identifierTable = new Hashtable<String, AIdentifierExpression>();
		for (int i = 0; i < identifiers.size(); i++) {
			AIdentifierExpression id = (AIdentifierExpression) identifiers
					.get(i);
			String name = Utils.getIdentifierAsString(id.getIdentifier());
			list.add(name);
			identifierTable.put(name, id);
		}

		Hashtable<String, PExpression> values = new Hashtable<String, PExpression>();
		ArrayList<AEqualPredicate> equalList = new ArrayList<AEqualPredicate>();
		analysePredicate(node.getPredicates(), list, values, equalList);

		ArrayList<ADomainExpression> parentDomainExprsList = collectParentDomainExpression(node
				.parent());

		// The set comprehension will be optimized if there is an equal node (
		// {x,y| x =
		// 1..} ) or the parent node is a domain expression (dom({..})).
		// There must be less equal nodes than quantified variables, otherwise
		// there
		// is no remaining variable to be quantified.
		// Moreover, the TLA+ syntax is restricted to non-nested tuples in a set
		// comprehension ({v : <<a,b>> \in S}.
		// Hence, there must be at most two remaining variables.
		// If these conditions are not fulfilled, the AST transformation will
		// not be applied.
		// However, other optimization techniques may be applicable.
		if ((values.size() > 0 || parentDomainExprsList.size() > 0)
				&& values.size() < list.size()
				&& list.size() - values.size() <= 2) {

			// delete nodes
			new NodesRemover(node.getPredicates(), equalList, values);

			int max = Math.min(list.size() - 1, parentDomainExprsList.size());
			int exprCount = list.size() - max;

			// {ids. ids2 \in {ids3 \in S: P } | exprs}
			ArrayList<PExpression> ids = new ArrayList<PExpression>();
			ArrayList<PExpression> ids2 = new ArrayList<PExpression>();
			ArrayList<PExpression> ids3 = new ArrayList<PExpression>();
			ArrayList<PExpression> exprs = new ArrayList<PExpression>();
			for (int i = 0; i < list.size(); i++) {
				String name = list.get(i);

				// expression list
				if (i < exprCount) {
					if (values.containsKey(name)) {
						exprs.add(values.get(name));
					} else {
						PExpression clone = (PExpression) identifierTable.get(
								name).clone();
						exprs.add(clone);
					}
				}

				// remaining quantified variables
				if (!values.containsKey(name)) {
					PExpression clone = (PExpression) identifierTable.get(name)
							.clone();
					ids.add(clone);
					PExpression clone2 = (PExpression) identifierTable
							.get(name).clone();
					ids2.add(clone2);
					PExpression clone3 = (PExpression) identifierTable
							.get(name).clone();
					ids3.add(clone3);
				}
			}

			AEventBComprehensionSetExpression eventBcomprehension = new AEventBComprehensionSetExpression();
			ACoupleExpression couple = new ACoupleExpression();
			couple.setList(exprs);
			eventBcomprehension.setExpression(couple);
			AMemberPredicate member = new AMemberPredicate();
			AComprehensionSetExpression compre = new AComprehensionSetExpression();
			eventBcomprehension.setIdentifiers(ids);
			if (ids.size() == 1) {
				member.setLeft(ids2.get(0));
			} else {
				ACoupleExpression couple2 = new ACoupleExpression(ids2);
				member.setLeft(couple2);
			}
			compre.setIdentifiers(ids3);
			compre.setPredicates(node.getPredicates());
			member.setRight(compre);

			eventBcomprehension.setPredicates(member);
			setSourcePosition(node, eventBcomprehension);
			if (parentDomainExprsList.size() > 0) {
				ADomainExpression aDomainExpression = parentDomainExprsList
						.get(max - 1);
				aDomainExpression.replaceBy(eventBcomprehension);
			} else {
				node.replaceBy(eventBcomprehension);
			}
			// eventBcomprehension.apply(this);
		} else {
			// node.getPredicates().apply(this);
		}
	}

	/**
	 * This method collects all {@link ADomainExpression} which are direct
	 * parents of the the set comprehension. For example the set comprehension
	 * in k = dom(dom({a,b,c| ..}) has 2 preceding dom expression. All preceding
	 * dom expression nodes are collected in the parameter domExprList.
	 * 
	 * @param node
	 * @return
	 */

	private ArrayList<ADomainExpression> collectParentDomainExpression(Node node) {
		if (node instanceof ADomainExpression) {
			ArrayList<ADomainExpression> domExprList = collectParentDomainExpression(node
					.parent());
			domExprList.add(0, (ADomainExpression) node); // prepend the
															// node
			return domExprList;
		} else {
			return new ArrayList<ADomainExpression>();
		}

	}

	private void setSourcePosition(AComprehensionSetExpression from,
			AEventBComprehensionSetExpression to) {
		to.setStartPos(from.getStartPos());
		to.setEndPos(from.getEndPos());
	}

	private void analysePredicate(PPredicate predicate, ArrayList<String> list,
			Hashtable<String, PExpression> values,
			ArrayList<AEqualPredicate> equalList) {
		if (predicate instanceof AConjunctPredicate) {
			AConjunctPredicate con = (AConjunctPredicate) predicate;
			analysePredicate(con.getLeft(), list, values, equalList);
			analysePredicate(con.getRight(), list, values, equalList);
		} else if (predicate instanceof AEqualPredicate) {
			AEqualPredicate equal = (AEqualPredicate) predicate;
			if (equal.getLeft() instanceof AIdentifierExpression) {
				AIdentifierExpression id = (AIdentifierExpression) equal
						.getLeft();
				String name = Utils.getIdentifierAsString(id.getIdentifier());
				Set<String> names = new HashSet<String>(values.keySet());
				names.add(name);
				if (list.contains(name)
						&& !DependenciesDetector.expressionContainsIdentifier(
								equal.getRight(), names)) {
					equalList.add(equal);
					values.put(name, equal.getRight());
				}
			} else if (!equalList.contains(equal)
					&& equal.getRight() instanceof AIdentifierExpression) {
				AIdentifierExpression id = (AIdentifierExpression) equal
						.getRight();
				String name = Utils.getIdentifierAsString(id.getIdentifier());
				Set<String> names = new HashSet<String>(values.keySet());
				names.add(name);
				if (list.contains(name)
						&& !DependenciesDetector.expressionContainsIdentifier(
								equal.getLeft(), names)) {
					equalList.add(equal);
					values.put(name, equal.getLeft());
				}
			}

		}

	}

	static class DependenciesDetector extends DepthFirstAdapter {
		private final Set<String> names;
		private boolean hasDependency = false;

		private DependenciesDetector(Set<String> names) {
			this.names = names;
		}

		@Override
		public void caseAIdentifierExpression(AIdentifierExpression node) {
			String name = Utils.getIdentifierAsString(node.getIdentifier());
			if (names.contains(name)) {
				hasDependency = true;
			}
		}

		static boolean expressionContainsIdentifier(PExpression node,
				Set<String> names) {
			DependenciesDetector dependenciesDetector = new DependenciesDetector(
					names);
			node.apply(dependenciesDetector);
			return dependenciesDetector.hasDependency;
		}

	}

	class NodesRemover extends DepthFirstAdapter {
		final ArrayList<AEqualPredicate> removeList;
		final Hashtable<String, PExpression> values;

		public NodesRemover(PPredicate predicate,
				ArrayList<AEqualPredicate> equalList,
				Hashtable<String, PExpression> values) {
			this.removeList = equalList;
			this.values = values;

			for (AEqualPredicate pred : removeList) {
				pred.replaceBy(null);
			}

			predicate.apply(this);

		}

		@Override
		public void caseAConjunctPredicate(AConjunctPredicate node) {
			if (node.getLeft() != null) {
				node.getLeft().apply(this);
			}
			if (node.getRight() != null) {
				node.getRight().apply(this);
			}
			outAConjunctPredicate(node);
		}

		@Override
		public void outAConjunctPredicate(AConjunctPredicate node) {
			if (node.parent() != null) {
				if (node.getLeft() == null && node.getRight() == null) {
					node.replaceBy(null);
				} else if (node.getLeft() == null) {
					PPredicate right = node.getRight();
					node.replaceBy(right);
				} else if (node.getRight() == null) {
					node.replaceBy(node.getLeft());
				}
			}
		}

		@Override
		public void caseAIdentifierExpression(AIdentifierExpression node) {
			String name = Utils.getIdentifierAsString(node.getIdentifier());

			PExpression value = values.get(name);
			if (value != null) {
				node.replaceBy((PExpression) value.clone());
			}

		}
	}

}
