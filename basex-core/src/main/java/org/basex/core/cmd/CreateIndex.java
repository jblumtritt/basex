package org.basex.core.cmd;

import static org.basex.core.Text.*;

import java.io.*;

import org.basex.core.*;
import org.basex.core.parse.*;
import org.basex.core.parse.Commands.*;
import org.basex.core.users.*;
import org.basex.data.*;
import org.basex.index.*;
import org.basex.util.*;
import org.basex.util.ft.*;

/**
 * Evaluates the 'create db' command and creates a new index.
 *
 * @author BaseX Team 2005-16, BSD License
 * @author Christian Gruen
 */
public final class CreateIndex extends ACreate {
  /**
   * Default constructor.
   * @param type index type, defined in {@link CmdIndex}
   */
  public CreateIndex(final Object type) {
    super(Perm.WRITE, true, type != null ? type.toString() : null);
  }

  @Override
  protected boolean run() {
    final Data data = context.data();

    final CmdIndex ci = getOption(CmdIndex.class);
    if(ci == null) return error(UNKNOWN_CMD_X, this);
    final IndexType type;
    if(ci == CmdIndex.TEXT) {
      type = IndexType.TEXT;
      data.meta.createtext = true;
    } else if(ci == CmdIndex.ATTRIBUTE) {
      type = IndexType.ATTRIBUTE;
      data.meta.createattr = true;
    } else if(ci == CmdIndex.TOKEN) {
      type = IndexType.TOKEN;
      data.meta.createtoken = true;
    } else if(ci == CmdIndex.FULLTEXT) {
      type = IndexType.FULLTEXT;
      data.meta.createft = true;
      data.meta.stemming = options.get(MainOptions.STEMMING);
      data.meta.casesens = options.get(MainOptions.CASESENS);
      data.meta.diacritics = options.get(MainOptions.DIACRITICS);
      data.meta.language = Language.get(options);
      data.meta.stopwords = options.get(MainOptions.STOPWORDS);
    } else {
      return error(UNKNOWN_CMD_X, this);
    }
    data.meta.names(type, options);
    data.meta.splitsize = options.get(MainOptions.SPLITSIZE);

    if(!startUpdate(data)) return false;
    boolean ok = true;
    try {
      create(type, data, this);
      ok = info(INDEX_CREATED_X_X, type, job().performance);
    } catch(final IOException ex) {
      ok = error(Util.message(ex));
    } finally {
      ok &= finishUpdate(data);
    }
    return ok;
  }

  @Override
  public void build(final CmdBuilder cb) {
    cb.init(Cmd.CREATE + " " + CmdCreate.INDEX).args();
  }

  /**
   * Builds the index structures.
   * @param data data reference
   * @param cmd calling command
   * @throws IOException I/O exception
   */
  static void create(final Data data, final ACreate cmd) throws IOException {
    if(data.meta.createtext) create(IndexType.TEXT, data, cmd);
    if(data.meta.createattr) create(IndexType.ATTRIBUTE, data, cmd);
    if(data.meta.createtoken) create(IndexType.TOKEN, data, cmd);
    if(data.meta.createft) create(IndexType.FULLTEXT, data, cmd);
  }

  /**
   * Builds the specified index.
   * @param type index to be built
   * @param data data reference
   * @param cmd calling command
   * @throws IOException I/O exception
   */
  static void create(final IndexType type, final Data data, final ACreate cmd) throws IOException {
    DropIndex.drop(type, data);
    data.createIndex(type, cmd);
    data.meta.index(type, true);
  }
}
