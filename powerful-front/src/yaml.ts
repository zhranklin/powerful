import * as yaml from "js-yaml";
import {DumpOptions} from "js-yaml";

const yamlDumpOption: DumpOptions = {
  noArrayIndent: true,
  sortKeys: true
};
export function toYaml(data: any) {
  return yaml.dump(data, yamlDumpOption)
}
export function fromYaml(str: string) {
  return yaml.load(str);
}
