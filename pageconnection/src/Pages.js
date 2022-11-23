import {BrowserRouter, Route, Navigate, Routes} from 'react-router-dom'
import Login from './Login';
import App from './App'
import Account from './Account';
import AddAccount from './AddAccount';

function Pages(){
    return(
        // <BrowserRouter>
        <Routes>
            {/* <Route path="/" element={ <Login/> }></Route> */}
            <Route path="/" element={<Login/>} />
            <Route path="/account" element={<App/>} />
            <Route path="/account/:id" element={<Account/>} />
            <Route path="/addAccount" element={<AddAccount/>} />


        </Routes>
        // </BrowserRouter>
    );
}

export default Pages;
