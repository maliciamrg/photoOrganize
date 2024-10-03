import logo from './logo.svg';
import './App.css';
import {Component} from "@react-buddy/ide-toolbox";
import Header from "./components/Header";
import Button from "./components/Button";


function App() {

    return (
        <div className="App">
            <Header className="App-header" version='44.55.66'>bob</Header>
            <Button color= "pink" text="Clik Me !"></Button>
            <Button color= "green" text="nounou" onClick={onClick}></Button>
            <Button color= "bleu" text="Bob !"></Button>
            <Button color= "cyan" text="Foo"></Button>
            <Button color= "pink" text="Clik Not Me !"></Button>
            <Button color= "pink" text="Poor Clik"></Button>
            <Button></Button>
        </div>
    );
}

const onClick = (e) => {console.log('nounou');};

export default App;
