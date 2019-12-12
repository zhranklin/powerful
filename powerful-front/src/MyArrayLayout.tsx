import * as React from 'react';
import {useCallback} from 'react';


import {ArrayLayoutProps, isObjectArrayWithNesting, rankWith,} from '@jsonforms/core';
import {Hidden} from '@material-ui/core';
import {withJsonFormsArrayLayoutProps} from '@jsonforms/react';
import {MaterialArrayLayout} from "@jsonforms/material-renderers/lib/layouts/MaterialArrayLayout";


export class MyMaterialArrayLayout extends MaterialArrayLayout {
  isExpanded = (index: number) => true;
}

export const MyMaterialArrayLayoutRenderer =
  ({ visible, enabled, id, uischema, schema, label, rootSchema, renderers, data, path, errors, addItem }: ArrayLayoutProps) => {
    const addItemCb = useCallback((p: string, value: any) => addItem(p, value), [addItem]);
    return (
      <Hidden xsUp={!visible}>
        <MyMaterialArrayLayout
          label={label}
          uischema={uischema}
          schema={schema}
          id={id}
          rootSchema={rootSchema}
          errors={errors}
          enabled={enabled}
          visible={visible}
          data={data}
          path={path}
          addItem={addItemCb}
          renderers={renderers}
        />
      </Hidden>
    );
  };

export default { tester: rankWith(5, isObjectArrayWithNesting), renderer: withJsonFormsArrayLayoutProps(MyMaterialArrayLayoutRenderer)};
