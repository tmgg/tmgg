import * as Icons from '@ant-design/icons';

declare type NamedIconProps = {
    name: string
};

export function NamedIcon(props:NamedIconProps){
    let {name, ...rest} = props;
    const IconType = Icons[name]

    return <IconType {...rest}></IconType>
}
