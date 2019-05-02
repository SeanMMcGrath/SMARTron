import React from 'react';
import PropTypes from 'prop-types';
import { withStyles } from '@material-ui/core/styles';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import Paper from '@material-ui/core/Paper';
import { Cell, BarChart, Bar, CartesianGrid, XAxis, YAxis, Tooltip, Legend } from "recharts";

const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#E6E6FA'];

const styles = theme => ({
    root: {
        width: '80%',
        marginTop: theme.spacing.unit * 3,
        overflowX: 'auto',
    },
    table: {
        minWidth: 500,
    },
});

let id = 0;
function createData(mean, median, max, min, range, deviation, variance, kr20, kr21, cronbach) {
    id += 1;
    return { mean, median, max, min, range, deviation, variance, kr20, kr21, cronbach };
}

const rows = [];

let i = 0;
function create_Data(Value, Scores) {
    i += 1;
    return { Value, Scores };
}

const bar = [];

// statsJSON.gradeDistribution.forEach((stat) => {
//     data.push(createData(stat.grade, stat.percent));
// });

fetch('http://pi.cs.oswego.edu:13126/statistics')
    .then((res) => res.json())
    .then((responseJson) => {
        rows.push(createData(responseJson.mean, responseJson.median, responseJson.max, responseJson.min, responseJson.range,
            responseJson.deviation, responseJson.variance, responseJson.kr20, responseJson.kr21, responseJson.cronbach));

        responseJson.byLetterGrade.forEach((q) => {
            bar.push(create_Data(q.Value, q.Scores));
        });
    })
    .catch((error) => {
        console.error(error);
    });

function StatsTable(props) {

    const { classes } = props;

    return (
        <div>
            <Paper className={classes.root}>
                <Table className={classes.table}>
                    <TableHead>
                        <TableRow>
                            <TableCell align="center">Mean</TableCell>
                            <TableCell align="center">Median</TableCell>
                            <TableCell align="center">Max</TableCell>
                            <TableCell align="center">Min</TableCell>
                            <TableCell align="center">Range</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {rows.map(row => (
                            <TableRow key={row.id}>
                                <TableCell component="th" scope="row">
                                    {row.mean}
                                </TableCell>
                                <TableCell align="center">{row.median}</TableCell>
                                <TableCell align="center">{row.max}</TableCell>
                                <TableCell align="center">{row.min}</TableCell>
                                <TableCell align="center">{row.range}</TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </Paper>

            <span>&nbsp;&nbsp;&nbsp;&nbsp;</span>

            <Paper className={classes.root}>
                <Table className={classes.table}>
                    <TableHead>
                        <TableRow>
                            <TableCell align="center">Standard Deviation</TableCell>
                            <TableCell align="center">Variance</TableCell>
                            <TableCell align="center">Kuder-Richardson-20</TableCell>
                            <TableCell align="center">Kuder-Richardson-21</TableCell>
                            <TableCell align="center">Cronbach</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {rows.map(row => (
                            <TableRow key={row.id}>
                                <TableCell component="th" scope="row">
                                    {row.deviation}
                                </TableCell>
                                <TableCell align="center">{row.variance}</TableCell>
                                <TableCell align="center">{row.kr20}</TableCell>
                                <TableCell align="center">{row.kr21}</TableCell>
                                <TableCell align="center">{row.cronbach}</TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </Paper>

            <span>&nbsp;&nbsp;&nbsp;&nbsp;</span>

            <BarChart width={700} height={200} data={bar}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="Value" />
                <YAxis />
                <Tooltip />
                <Legend />
                <Bar data={bar} dataKey="Value" dataKey="Scores" fill="#000000">
                    {
                        bar.map((entry, index) => <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />)
                    }
                </Bar>
            </BarChart>
        </div >
    );
}

StatsTable.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default withStyles(styles)(StatsTable);