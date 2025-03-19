import {CommonViewProps} from "./index";
// @ts-ignore
import React from "react";


declare type ViewEllipsisProps = {
    maxLength?:  number,
} & CommonViewProps;

export class ViewEllipsis extends React.Component<ViewEllipsisProps, any> {
}
