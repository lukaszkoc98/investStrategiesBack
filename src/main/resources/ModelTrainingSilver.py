#Importing Libraries
import pandas as pd
from datetime import datetime
import matplotlib.pyplot as plt
from pycaret.regression import *
from yahoofinancials import YahooFinancials
import sys

ticker_details = pd.read_excel("E:/POLSL/inzynierka/projekt/investStrategiesBack/src/main/resources/Ticker List.xlsx", engine='openpyxl')
ticker_details.head(20)

ticker = ticker_details['Ticker'].to_list()
names = ticker_details['Description'].to_list()

#Extracting Data from Yahoo Finance and Adding them to Values table using date as key
end_date= "2021-11-25"
start_date = "2010-01-01"
date_range = pd.bdate_range(start=start_date,end=end_date)
values = pd.DataFrame({ 'Date': date_range})
values['Date']= pd.to_datetime(values['Date'])

#Extracting Data from Yahoo Finance and Adding them to Values table using date as key
for i in ticker:
    raw_data = YahooFinancials(i)
    raw_data = raw_data.get_historical_price_data(start_date, end_date, "daily")
    singledf = pd.DataFrame(raw_data[i]['prices'])
    df = singledf[['formatted_date','adjclose']]
    df.columns = ['Date1',i]
    df['Date1']= pd.to_datetime(df['Date1'])
    values = values.merge(df,how='left',left_on='Date',right_on='Date1')
    values = values.drop(labels='Date1',axis=1)

#Renaming columns to represent instrument names rather than their ticker codes for ease of readability
names.insert(0,'Date')
values.columns = names
print(values.shape)
print(values.isna().sum())
values.tail()

#Front filling the NaN values in the data set
values = values.fillna(method="ffill",axis=0)
values = values.fillna(method="bfill",axis=0)
values.isna().sum()

# Co-ercing numeric type to all columns except Date
cols=values.columns.drop('Date')
values[cols] = values[cols].apply(pd.to_numeric,errors='coerce').round(decimals=1)
values.tail()

values.to_csv("E:/POLSL/inzynierka/projekt/investStrategiesBack/src/main/resources/Training Data_Values Silver.csv")

imp = ['Gold','Silver', 'Crude Oil', 'S&P500','MSCI EM ETF']
# Calculating Short term -Historical Returns
change_days = [1,3,5,14,21]

data = pd.DataFrame(data=values['Date'])
for i in change_days:
    print(data.shape)
    x= values[cols].pct_change(periods=i).add_suffix("-T-"+str(i))
    data=pd.concat(objs=(data,x),axis=1)
    x=[]
print(data.shape)

# Calculating Long term Historical Returns
change_days = [60,90,180,250]

for i in change_days:
    print(data.shape)
    x= values[imp].pct_change(periods=i).add_suffix("-T-"+str(i))
    data=pd.concat(objs=(data,x),axis=1)
    x=[]
print(data.shape)


#Calculating Moving averages for Silver
moving_avg = pd.DataFrame(values['Date'],columns=['Date'])
moving_avg['Date']=pd.to_datetime(moving_avg['Date'],format='%Y-%b-%d')
moving_avg['Silver/15SMA'] = (values['Silver']/(values['Silver'].rolling(window=15).mean()))-1
moving_avg['Silver/30SMA'] = (values['Silver']/(values['Silver'].rolling(window=30).mean()))-1
moving_avg['Silver/60SMA'] = (values['Silver']/(values['Silver'].rolling(window=60).mean()))-1
moving_avg['Silver/90SMA'] = (values['Silver']/(values['Silver'].rolling(window=90).mean()))-1
moving_avg['Silver/180SMA'] = (values['Silver']/(values['Silver'].rolling(window=180).mean()))-1
moving_avg['Silver/90EMA'] = (values['Silver']/(values['Silver'].ewm(span=90,adjust=True,ignore_na=True).mean()))-1
moving_avg['Silver/180EMA'] = (values['Silver']/(values['Silver'].ewm(span=180,adjust=True,ignore_na=True).mean()))-1
moving_avg = moving_avg.dropna(axis=0)
print(moving_avg.shape)
moving_avg.head()


#Merging Moving Average values to the feature space
print(data.shape)
data['Date']=pd.to_datetime(data['Date'],format='%Y-%b-%d')
data = pd.merge(left=data,right=moving_avg,how='left',on='Date')
print(data.shape)
data.isna().sum()


#Caluculating forward returns for Target
y = pd.DataFrame(data=values['Date'])
print(y.shape)
y['Silver-T+14']=values["Silver"].pct_change(periods=-14)
y['Silver-T+22']=values["Silver"].pct_change(periods=-22)
print(y.shape)
y.isna().sum()

# Removing NAs
print(data.shape)
data = data[data['Silver-T-250'].notna()]
y = y[y['Silver-T+22'].notna()]
print(data.shape)
print(y.shape)

#Adding Target Variables
data = pd.merge(left=data,right=y,how='inner',on='Date',suffixes=(False,False))
print(data.shape)
data.isna().sum()

data.to_csv("Training Data Silver.csv",index=False)

corr = data.corr().iloc[:,-2:].drop(labels=['Silver-T+14','Silver-T+22'],axis=0)

import seaborn as sns
import numpy as np

sns.distplot(corr.iloc[:,0])

pd.set_option('display.max_rows', None)
corr_data = data.tail(2000).corr()
corr_data = pd.DataFrame(corr_data['Silver-T+14'])
#corr_data = corr_data.iloc[3:,]
corr_data = corr_data.sort_values('Silver-T+14',ascending=False)
#corr_data
sns.distplot(corr_data)

#data = pd.read_csv("Training Data.csv")


data_22= data.drop(['Silver-T+14'],axis=1)
data_22.head()

a=setup(data_22,target='Silver-T+22', ignore_features=['Date'],session_id=11, silent=True,profile=False,remove_outliers=False)

compare_models(include=['knn','et'],turbo=True)

knn = create_model('knn')

knn_tuned = tune_model(knn,n_iter=150)

print (knn_tuned)



et = create_model('et')

et_tuned = tune_model(et)

evaluate_model(knn_tuned)

evaluate_model(et)

b=setup(data_22,target='Silver-T+22', ignore_features=['Date'],session_id=11, silent=True,profile=False,remove_outliers=True)

knn_tuned = tune_model(knn,n_iter=150)

et = create_model('et')

lightgbm = create_model('lightgbm')

et_bagged = ensemble_model(et,method='Bagging')

knn_tuned_bagged = ensemble_model(knn_tuned, method='Bagging')

blend_knn_et = blend_models(estimator_list=[knn_tuned,et])

# stack1 = create_stacknet(estimator_list=[[lightgbm,knn_tuned],[et,blend_knn_et]],restack=True)

# stack2 = create_stacknet(estimator_list=[[lightgbm,et,knn_tuned],[blend_knn_et]], restack=True)

# stack3 = create_stacknet(estimator_list=[[lightgbm,et,knn_tuned],[blend_knn_et]], restack=True,meta_model=blend_knn_et)

save_model(model=blend_knn_et, model_name='22DaySilver')