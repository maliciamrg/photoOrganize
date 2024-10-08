const Button = (props) => {

    return (
        <button
            onClick={props.onClick}
            style={{backgroundColor: props.color}}
            className='btn'>{props.text}</button>
    )
}

const onClickDefault = (e) => {console.log('onClick', e );};

Button.defaultProps = {text: "Click", color: "white", onClick: onClickDefault}

export default Button
