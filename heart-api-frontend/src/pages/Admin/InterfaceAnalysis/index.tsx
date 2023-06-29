import {PageContainer,} from '@ant-design/pro-components';
import '@umijs/max';
import React, {useEffect, useState} from 'react';
import ReactECharts from 'echarts-for-react';
import {listTopInterfaceInfoInvokeUsingGET} from "@/services/heartApi-backend/analysisController";


/**
 * 接口分析
 * @constructor
 */
const InterfaceAnalysis: React.FC = () => {

  const [data, setData] = useState<API.InterfaceVo[]>([]);
  const [loading, setLoading] = useState(true);


  useEffect(() => {
    try {
      listTopInterfaceInfoInvokeUsingGET().then(res => {
        if (res.data) {
          setData(res.data);
          setLoading(false)
        }
      })
    } catch (e: any) {

    }
  }, [])



  //映射
  const chartData = data.map(item => {
    return {
      value: item.totalNum,
      name: item.name
    }
  })

  const options = {
    title: {
      text: '热点接口调用数据TOP3',
      subtext: '分析饼图',
      left: 'center'
    },
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: 'Access From',
        type: 'pie',
        radius: '50%',
        data: chartData,
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  };


  return (
    <PageContainer>
      <ReactECharts showLoading={loading} option={options}/>
    </PageContainer>
  );
};
export default InterfaceAnalysis;
