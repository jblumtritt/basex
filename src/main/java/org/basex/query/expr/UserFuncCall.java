package org.basex.query.expr;

import org.basex.query.QueryContext;
import org.basex.query.QueryException;
import org.basex.query.item.Item;
import org.basex.query.item.QNm;
import org.basex.query.item.Value;
import org.basex.query.iter.Iter;
import org.basex.util.InputInfo;

/**
 * A tail-recursive function call.
 * @author Leo Woerteler
 */
public final class UserFuncCall extends AFuncCall {
  /**
   * Constructor.
   * @param ii input info
   * @param nm name of the function to call
   * @param arg arguments
   */
  public UserFuncCall(final InputInfo ii, final QNm nm, final Expr[] arg) {
    super(ii, nm, arg);
  }

  @Override
  public Item item(final QueryContext ctx, final InputInfo ii)
      throws QueryException {

    // cache arguments, evaluate function and reset variable scope
    final int s = addArgs(ctx, args(ctx));
    final Item it = func.item(ctx, ii);
    ctx.vars.reset(s);
    return it;
  }

  @Override
  public Value value(final QueryContext ctx) throws QueryException {
    // cache arguments, evaluate function and reset variable scope
    final int s = addArgs(ctx, args(ctx));
    final Value v = func.value(ctx);
    ctx.vars.reset(s);
    return v;
  }

  @Override
  public Iter iter(final QueryContext ctx) throws QueryException {
    // [LW] make result streamable
    return value(ctx).iter();
  }

}
