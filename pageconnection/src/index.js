import React from 'react';
import ReactDOM from 'react-dom/client';
import Pages from './Pages';
import Login from './Login'
import App from './App'
import Profile from './components/Profile';
import MiniAccounts from './components/MiniAccounts'
import Account from './Account';
import {BrowserRouter} from 'react-router-dom'

// ReactDOM.render(
//   <React.StrictMode>
//     <Pages></Pages>
//   </React.StrictMode>,
//   document.getElementById('root')
// );

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <BrowserRouter >
    {/* <App /> */}
    <Pages></Pages>
  </BrowserRouter>
);


// ReactDOM.render(
//   <React.StrictMode>
//     <BrowserRouter>
//       <Routes>
//         <Route path="/" element={ <App /> }>
//         </Route>
//       </Routes>
//     </BrowserRouter>
//   </React.StrictMode>,
//   document.getElementById('root')
// );

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
// reportWebVitals();
