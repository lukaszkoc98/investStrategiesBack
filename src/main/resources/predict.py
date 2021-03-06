#Importing Libraries
import pandas as pd
from datetime import datetime, timedelta
import matplotlib.pyplot as plt
from yahoofinancials import YahooFinancials
import sys

metal = sys.argv[1]

ticker_details = pd.read_excel("E:/POLSL/inzynierka/projekt/investStrategiesBack/src/main/resources/Ticker List.xlsx", engine='openpyxl')
ticker = ticker_details['Ticker'].to_list()
names = ticker_details['Description'].to_list()
#Preparing Date Range
end_date= datetime.strftime(datetime.today(),'%Y-%m-%d')
time_delta = timedelta(days = 380)
start_date = datetime.strftime(datetime.today() - time_delta,'%Y-%m-%d')
date_range = pd.bdate_range(start=start_date,end=end_date)
values = pd.DataFrame({ 'Date': date_range})
values['Date']= pd.to_datetime(values['Date'])
#Extracting Data from Yahoo Finance and Adding them to Values table using date as key
for i in ticker:
    raw_data = YahooFinancials(i)
    raw_data = raw_data.get_historical_price_data(start_date, end_date, "daily")
    df = pd.DataFrame(raw_data[i]['prices'])[['formatted_date','adjclose']]
    df.columns = ['Date1',i]
    df['Date1']= pd.to_datetime(df['Date1'])
    values = values.merge(df,how='left',left_on='Date',right_on='Date1')
    values = values.drop(labels='Date1',axis=1)
#Renaming columns to represent instrument names rather than their ticker codes for ease of readability
names.insert(0,'Date')
values.columns = names
#Front filling the NaN values in the data set
values = values.fillna(method="ffill",axis=0)
values = values.fillna(method="bfill",axis=0)
# Co-ercing numeric type to all columns except Date
cols=values.columns.drop('Date')
values[cols] = values[cols].apply(pd.to_numeric,errors='coerce').round(decimals=1)
imp = ['Gold','Silver', 'Crude Oil', 'S&P500','MSCI EM ETF']
# Calculating Short term -Historical Returns
change_days = [1,3,5,14,21]
data = pd.DataFrame(data=values['Date'])
for i in change_days:
    x= values[cols].pct_change(periods=i).add_suffix("-T-"+str(i))
    data=pd.concat(objs=(data,x),axis=1)
    x=[]
# Calculating Long term Historical Returns
change_days = [60,90,180,250]
for i in change_days:
    x= values[imp].pct_change(periods=i).add_suffix("-T-"+str(i))
    data=pd.concat(objs=(data,x),axis=1)
    x=[]
#Calculating Moving averages for Metal
moving_avg = pd.DataFrame(values['Date'],columns=['Date'])
moving_avg['Date']=pd.to_datetime(moving_avg['Date'],format='%Y-%b-%d')
moving_avg[metal + '/15SMA'] = (values[metal]/(values[metal].rolling(window=15).mean()))-1
moving_avg[metal + '/30SMA'] = (values[metal]/(values[metal].rolling(window=30).mean()))-1
moving_avg[metal + '/60SMA'] = (values[metal]/(values[metal].rolling(window=60).mean()))-1
moving_avg[metal + '/90SMA'] = (values[metal]/(values[metal].rolling(window=90).mean()))-1
moving_avg[metal + '/180SMA'] = (values[metal]/(values[metal].rolling(window=180).mean()))-1
moving_avg[metal + '/90EMA'] = (values[metal]/(values[metal].ewm(span=90,adjust=True,ignore_na=True).mean()))-1
moving_avg[metal + '/180EMA'] = (values[metal]/(values[metal].ewm(span=180,adjust=True,ignore_na=True).mean()))-1
moving_avg = moving_avg.dropna(axis=0)
#Merging Moving Average values to the feature space
data['Date']=pd.to_datetime(data['Date'],format='%Y-%b-%d')
data = pd.merge(left=data,right=moving_avg,how='left',on='Date')
data = data[data[metal + '-T-250'].notna()]
prediction_data = data.copy()

from pycaret.regression import *
#Loading the stored model
regressor_22 = load_model("E:/POLSL/inzynierka/projekt/investStrategiesBack/src/main/resources/22Day" + metal)
#Making Predictions
predicted_return_22 = predict_model(regressor_22,data=prediction_data)
predicted_return_22=predicted_return_22[['Date','Label']]
predicted_return_22.columns = ['Date','Return_22']
#Adding return Predictions to Gold Values
predicted_values = values[['Date',metal]]
predicted_values = predicted_values.tail(len(predicted_return_22))
predicted_values = pd.merge(left=predicted_values,right=predicted_return_22,on=['Date'],how='inner')
predicted_values[metal + '-T+22']=(predicted_values[metal]*(1+predicted_values['Return_22'])).round(decimals =1)
#Adding T+22 Date
from datetime import datetime, timedelta
predicted_values['Date-T+22'] = predicted_values['Date']+timedelta(days = 22)
predicted_values.tail()

predicted_values.to_excel("E:/POLSL/inzynierka/projekt/investStrategiesBack/src/main/resources/" + metal + ".xlsx")
