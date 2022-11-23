import AccountList from "./components/AccountList";
import Profile from "./components/Profile";
import './css/Account.css'
import Money from './images/Money.png';
import { useParams } from "react-router-dom";
import { useEffect } from "react";
import axios from "axios"
import { useState } from "react";

import CardImg from './images/Card.png'


function Account() {
  const {id} =useParams()
  const [lo, setLo]=useState(false)

  let token = JSON.parse(localStorage.getItem('user'));

  const [balance, setBalance] = useState("");
  const [currency, setCurrency]=useState("")
  const [iban, setIban] = useState("");
  const [interest, setinterest] = useState("");


      //GET A ACCOUNT BY ID
      useEffect(()=>{
        if(lo===false){
            if(id){ 
            axios.get(`http://localhost:8080/account/${id}`
            , { headers: {"Authorization" : `Bearer ${token}`}}
            ).then(rs=>{
                console.log("Acc with desired id",rs.data)
                setLo(true)
                setBalance(rs.data.balance)
                setCurrency(rs.data.currency)
                setIban(rs.data.iban)
                setinterest(rs.data.interest)
        })
        .catch(err => {
            console.log(err);
        })
    }}},[id]);


  const [cardId, setCardId] = useState("")
  const [cardType, setCardType] = useState("");
  const [ccNumber, setCcNumber]=useState("")


      useEffect(()=>{
        if(lo===false){
            if(id){ 
            axios.get(`http://localhost:8080/card/${id}`
            , { headers: {"Authorization" : `Bearer ${token}`}}
            ).then(rs=>{
                console.log("Card with desired id",rs.data)
                setLo(true)
                setCardType(rs.data.cardType)
                setCardId(rs.data.cardId)
                setCcNumber(rs.data.ccNumber)
        })
        .catch(err => {
            console.log(err);
        })
    }}},[id]);


  return(
    <div className="panel">
    <Profile />
    <div className='accountList'>
      <div className='accountCard'>
        <div className="account">
            <img src={Money} alt="" className="moneyImg"/>
            <h4>Account number: {id}</h4>
            <p>{currency} {balance}</p>
            <p>IBAN: {iban}</p>
            <p>Interest: {interest}</p>
            <div className="accountMethods">
              <div className="createCard">
                  <button className="createCardButton"><p>Create Card</p></button>
              </div>
            </div>
        </div>
        <div className="card">
            <img src={CardImg} alt="" className="cardImg"/>
            <h4>Card number {cardId}</h4>
            <p>ccNumber:  {ccNumber}</p>
            <p>Card Type: {cardType}</p>
            <div className="cardMethods">
              <div className="withdraw">
                  <button className="withdrawButton"><p>Withdraw</p></button>
              </div>
              <div className="deposit">
                  <button className="depositButton"><p>Deposit</p></button>
              </div>
              <div className="makeTransaction">
                  <button className="makeTransctionButton"><p>Make Transaction</p></button>
              </div>
              <div className="getAllTransactions">
                  <button className="getAllTransactionsButton"><p>Get all Transactions</p></button>
              </div>
            </div>
        </div>
      </div>
    </div>
  </div>
  );
}

export default Account;