import React from 'react';

export interface RemoteTreeSelectProps {
  value?: any;
  onChange?: (value: any) => void;
}

declare class FieldRemoteTreeSelect extends React.Component<RemoteTreeSelectProps, any> {}

