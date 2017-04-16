// Generated from Stmnt.g4 by ANTLR 4.7
package parser;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link StmntParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface StmntVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link StmntParser#prog}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProg(StmntParser.ProgContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Print}
	 * labeled alternative in {@link StmntParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrint(StmntParser.PrintContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Assign}
	 * labeled alternative in {@link StmntParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssign(StmntParser.AssignContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Arith}
	 * labeled alternative in {@link StmntParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArith(StmntParser.ArithContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ArithGroup}
	 * labeled alternative in {@link StmntParser#arithExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArithGroup(StmntParser.ArithGroupContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Add}
	 * labeled alternative in {@link StmntParser#arithExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAdd(StmntParser.AddContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Number}
	 * labeled alternative in {@link StmntParser#arithExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumber(StmntParser.NumberContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Mult}
	 * labeled alternative in {@link StmntParser#arithExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMult(StmntParser.MultContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Id}
	 * labeled alternative in {@link StmntParser#arithExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitId(StmntParser.IdContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Power}
	 * labeled alternative in {@link StmntParser#arithExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPower(StmntParser.PowerContext ctx);
}