import PropTypes from "prop-types";
const Header = (props) => {
    return (
        <div className={props.className}  >
            <header>
                <h1>PhotoOrganize {props.version}</h1>
            </header>
        </div>
    )
}

Header.defaultProps = {
    version: '1.0.0'
}

Header.prototype = {
    version: PropTypes.string.isRequired
}
export default Header
