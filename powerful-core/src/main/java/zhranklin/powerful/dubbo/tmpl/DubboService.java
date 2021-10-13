package zhranklin.powerful.dubbo.tmpl;

import zhranklin.powerful.dubbo.pojo.DubboPojo1;
import zhranklin.powerful.model.Instruction;

/**
 * Created by twogoods on 2019/10/29.
 */
public interface DubboService {
    Object echo(Integer n1, Instruction instruction);
    Object echo(Integer n1, Integer n2, Instruction instruction);
    Object complex(DubboPojo1 body, Instruction instruction);
}
