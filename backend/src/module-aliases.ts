import { addAliases } from 'module-alias';
import { join } from 'path';

addAliases({
  '@config': join(__dirname, 'config'),
  '@controllers': join(__dirname, 'controllers'),
  '@services': join(__dirname, 'services'),
  '@utils': join(__dirname, 'utils'),
  '@data': join(__dirname, 'data'),
  '@interfaces': join(__dirname, 'interfaces'),
});
