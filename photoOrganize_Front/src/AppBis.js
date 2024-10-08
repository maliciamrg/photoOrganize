import logo from './logoBis.png';
import './AppBis.css';

function AppBis() {
  return (
    <div className="AppBis">
      <header className="AppBis-header">
        <img src={logo} className="AppBis-logo" alt="logo" />
        <p>
          Edit <code>src/AppBis.js</code> and save to reload.
        </p>
        <a
          className="AppBis-link"
          href="https://reactjs.org"
          target="_blank"
          rel="noopener noreferrer"
        >
          Learn React
        </a>
      </header>
    </div>
  );
}

export default AppBis;
