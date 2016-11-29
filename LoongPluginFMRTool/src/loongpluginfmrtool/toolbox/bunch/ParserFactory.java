/****
 *
 *	$Log: ParserFactory.java,v $
 *	Revision 3.0  2002/02/03 18:41:54  bsmitc
 *	Retag starting at 3.0
 *
 *	Revision 1.1.1.1  2002/02/03 18:30:04  bsmitc
 *	CVS Import
 *
 *	Revision 3.0  2000/07/26 22:46:11  bsmitc
 *	*** empty log message ***
 *
 *	Revision 1.1.1.1  2000/07/26 22:43:34  bsmitc
 *	Imported CVS Sources
 *
 *
 */
package loongpluginfmrtool.toolbox.bunch;

import loongpluginfmrtool.toolbox.bunch.GenericFactory;
import loongpluginfmrtool.toolbox.bunch.Parser;

/**
 * A factory for parsers of different kinds
 *
 * @author Diego Doval
 * @version 1.0
 * @see loongpluginfmrtool.toolbox.bunch.Parser
 * @see loongpluginfmrtool.toolbox.bunch.GenericFactory
 */
public
class ParserFactory
  extends GenericFactory
{

public
ParserFactory()
{
  super();
  setFactoryType("Parser");
  addItem("dependency", "loongpluginfmrtool.toolbox.bunch.DependencyFileParser");
  addItem("gxl", "loongpluginfmrtool.toolbox.bunch.gxl.parser.GXLGraphParser");
  addItem("cluster", "loongpluginfmrtool.toolbox.bunch.ClusterFileParser");
}

public
Parser
getParser(String name)
{
  return (Parser)getItemInstance(name);
}
}
