import React from 'react';

export interface UploadImageProps {
  onChange?: (result: string) => void;
  value?: string;
  url?: string;
  businessKey?: string;
  accept?: string;
}

declare class FieldUploadImage extends React.Component<UploadImageProps, any> {}

declare class UploadImage extends React.Component<UploadImageProps, any> {}
