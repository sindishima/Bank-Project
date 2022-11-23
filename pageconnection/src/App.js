import Profile from './components/Profile'
import MiniAccounts from './components/MiniAccounts';
import './css/App.css'
import addIcon from './images/AddIcon.png'
import { Link } from "react-router-dom";
import AddAccountPopup from './AddAccountPopup'
import axios from 'axios'
import React, { Children, useEffect, useState } from "react";



function App() {

  const [balance, setBalance] = useState("");
  const [currency, setCurrency]=useState("")

      //API ALL ACCOUNTS 
      const [allAccounts, setAllAccounts]=useState([])
      const [lo, setLo]=useState(false);
  
      let token = JSON.parse(localStorage.getItem('user'));

      useEffect(()=>{
          if(lo===false){ 
          axios.get("http://localhost:8080/account/all"
          , { headers: {"Authorization" : `Bearer ${token}`}}
          ).then(rs=>{
              console.log("All acc",rs.data)
              setAllAccounts(rs.data)
              setLo(true)
          })
          .catch(err => {
              console.log(err);
          })
      }},[allAccounts, lo]);


  const [buttonPopup1, setButtonPopup1]=useState(false);

const handleSubmit1=()=>{
    const newAccount={balance, currency};
    axios.post("http://localhost:8080/account", newAccount
    , { headers: {"Authorization" : `Bearer ${token}`}}
    ).then(res=>{
        console.log("new account ",res.data)
        console.log(res.data)
    })
    if(newAccount){
        setAllAccounts.push(newAccount);
    }
}

  return(
  <div className='panel1'>
    <Profile />
    <div className='accountList1'>
      <div className='smallAccountList1'>
        {allAccounts.map((data) => ( 
            <MiniAccounts
                key={data.id}
                id={data.accountId}
                accountStatus={data.accountStatus}
                balance={data.balance}
                currency={data.currency}
                iban={data.iban}
              />
          ))}

          <AddAccountPopup trigger={buttonPopup1} setTrigger={setButtonPopup1}>
            <form onSubmit={handleSubmit1} >
                <label htmlFor="" className='currencyTxt'>Currency:</label><br />
                <input type="text" name="" id="" className='inputCurrency' value={currency} onChange={(event) => setCurrency(event.target.value)}/><br />
                <label htmlFor="" className='currencyTxt'>Bilance:</label><br />
                <input type="text" name="" id="" className='inputBalance' value={balance} onChange={(event) => setBalance(event.target.value)} />
                <button className='confirmPopup1'><p className='confirmPopupText1'>Confirm</p></button>
            </form>
          </AddAccountPopup>
            <button className="buttonPopupProduct" onClick={()=> setButtonPopup1(true)}><img src={addIcon} alt="" className="addIconImage" /></button>
        </div>
    </div>
  </div>
  ); 
}

export default App;
